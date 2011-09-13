package orderly.narcolepsy

// Maven
import org.apache.maven.artifact.versioning.{DefaultArtifactVersion => SoftwareVersion}

// Scala
import scala.xml._

// Spray
import cc.spray._
import http._ // To get HttpRequest etc
import cc.spray.client._

/**
 * NarcolepsyClient is an abstract class modelling an asynchronous client for a
 * a generic RESTful API. NarcolepsyClient is built on top of spray-client,
 * which is in turn based on Ning's xxx. spray-client uses Akka to xxx
 *
 * Extending NarcolepsyClient involves xxx
 * 
 * For a better understanding of RESTful web service clients, please see XXX
 */
abstract class NarcolepsyClient(
  val rootUri:      String = defaultRootUri,
  val contentType:  String = defaultContentType,
  val username:     String,
  val password:     String) { // TODO: change contentType to a Spray variable
                              // TODO: make a more general authentication input

  /* ----------------------------------------------------------------
   * Type definitions used by NarcolepsyClient
   */

  // TODO: remove this. Temporary to test the API client without fannying around with JAXB
  type RestfulRepresentation = String

  // The return type for an API response.
  // Holds return code, either one representation or multiple, and a flag
  // indicating whether the representation is an error or not.
  // TODO: look at how squeryl deals with returning one row or multiple
  type RestfulResponse = (Int, Either[RestfulRepresentation, List[RestfulRepresentation]], Bool)

  // Simple synonym for the API parameters
  type RestfulParams = Map[String, String]

  /* ----------------------------------------------------------------
   * Need to populate the below vals to define a new NarcolepsyClient
   */

  // To provide a human readable name for this client, e.g. "Shopify Scala client"
  val clientName: String

  // Define the software version, e.g. 1.1.0 or 2
  val clientVersion: SoftwareVersion

  // Define the format that errors are returned in
  // Valid formats are plaintext, representation or mixed
  // TODO: really this ought to be some sort of enum or something
  val errorFormat: String

  // To store the content types (e.g. XML, JSON) supported by this RESTful web service
  val supportedContentTypes: List[String] // TODO: change contentType to a Spray variable

  // The default content type if none is supplied
  val defaultContentType: String // TODO: change contentType to a Spray variable

  // The header variable which contains the version information
  // Set to None if there is no easily available version information in a header
  val versionHeader: String

  // The default root API URL if supplied. Only makes sense for APIs from SaaS companies with fixed API endpoints
  // Set to None if a default root API does not make sense for this API
  val defaultRootUri: String

  // To track which parameters are valid for GETs
  val validGetParams: Map[String, String] // TODO: think need a different way to represent this

  // The minimum version of the RESTful API supported. SoftwareVersion taken from Maven versioning
  // TODO: implement all this (quite bespoke per API?)
  val minVersionSupported: SoftwareVersion
  val maxVersionSupported: SoftwareVersion

  /* ----------------------------------------------------------------
   * Validation to check that the constructor arguments are okay
   */

  // First let's validate that we have a rootUri
  rootUri.getOrElse(throw new NarcolepsyClientException("rootUri missing, must be set for %s".format(clientName)))

  // Now let's validate that the content type passed in is legitimate for this API
  if (!supportedContentTypes contains contentType) {
    throw new NarcolepsyClientException("Content type " + contentType + " is not supported")
  }

  /* ----------------------------------------------------------------
   * Actual HTTP client constructor
   */

  // Finally build an asynchronous spray-client with custom configuration options
  val client = new HttpClient(ClientConfig(
    requestTimeoutInMs = 500,
    userAgent = "%s/%s [NarcolepsyClient]".format(clientName, clientVersion),
    useRawUrl = false
  ))

  /** ----------------------------------------------------------------
   * GET methods
   */

  /**
   * Retrieve (GET) a resource, self-assembly version without parameters
   * @param resource Type of resource to retrieve
   * @return XML response from the RESTful API
   */
  def get(resource: String): RestfulResponse = {
    get(resource, None, None)
  }

  /**
   * Retrieve (GET) a resource, self-assembly version with parameters
   * @param resource Type of resource to retrieve
   * @param params Map of parameters (one or more of 'filter', 'display', 'sort', 'limit')
   * @return XML response from the RESTful API
   */
  def get(resource: String, params: RestfulParams): RestfulResponse = {
    get(resource, None, Some(params))
  }

  /**
   * Retrieve (GET) a resource, self-assembly version without parameters
   * @param resource Type of resource to retrieve
   * @param id Resource ID to retrieve
   * @return XML response from the RESTful API
   */
  def get(resource: String, id: Int): RestfulResponse = {
    get(resource, Some(id), None)
  }

  /**
   * Retrieve (GET) a resource, self-assembly version with parameters
   * @param resource Type of resource to retrieve
   * @param id Resource ID to retrieve
   * @param params Map of parameters (one or more of 'filter', 'display', 'sort', 'limit')
   * @return XML response from the RESTful API
   */
  def get(resource: String, id: Int, params: RestfulParams): RestfulResponse = {
    get(resource, Some(id), Some(params))
  }

  /**
   * Retrieve (GET) a resource, helper version using Options
   * @param resource Type of resource to retrieve
   * @param id Optional resource ID to retrieve
   * @param params Optional Map of parameters (one or more of 'filter', 'display', 'sort', 'limit')
   * @return XML response from the RESTful API
   */
  protected def get(resource: String, id: Option[Int], params: Option[RestfulParams]): RestfulResponse = {
    getURL(
      resource +
      (if (id.isDefined) "/%d".format(id.get) else "") +
      (if (params.isDefined) "?%s".format(canonicalize(params.get)) else "")
    )
  }

  /**
   * Retrieve (GET) a resource, URL version
   * @param url A URL which explicitly sets the resource type and ID to retrieve
   * @return XML response from the RESTful API
   */
  def getURL(url: String): RestfulResponse = {
    marshal(resource, execute(HttpMethods.GET, url, None)) // Execute the API call, marshall the output into the appropriate representation for the resource
  }

  /** ----------------------------------------------------------------
   * Utility methods to support the GET, POST etc methods
   */

  /**
   * Handles an HTTP request to the web service.
   * @param method HttpMethod to apply to this request
   * @param requestUri Relative path to resource. Attach rootUri to get the absolute URI
   * @return A RestfulResponse object
   */
  protected def execute(
    requestMethod: HttpMethod,
    requestUri: String): RestfulResponse = {

    // TODO: let's add in the Accept header based on the contentType requested
    val request = new HttpRequest(
      method = requestMethod,
      uri = requestUri,
      headers = Nil,
      content = None, // TODO: this needs to be configurable
      remoteHost = None, // TODO: what is this?
      version = None // TODO: what is this?
    )
    val future = client.dispatch(request)
    val response = future.get // Block TODO: make this async capable

     // Return the RestfulResponse
    (200, Left(response), false) // TODO: populate with proper values
  }

  // TODO: do we need the below? Is there an equivalent in spray?
  /**
   * Returns a canonicalized, escaped string of &key=value pairs from a Map of parameters
   * @param params A map of parameters ('filter', 'display' etc)
   * @return A canonicalized escaped string of the parameters
   */
  protected def canonicalize(params: RestfulParams): String = {

    val nameValues = params.map { param => new BasicNameValuePair(param._1, param._2) }
    URLEncodedUtils.format(nameValues.toSeq.asJava, CHARSET)
  }

  // TODO: about the below: don't assume XML returned (might be JSON)
  // TODO: need to think about how the validation will work (taking it out for now)
}