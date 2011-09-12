package orderly.narcolepsy

// Scala
import scala.xml._

// Spray
import cc.spray._
import http._ // To get HttpRequest etc
import cc.spray.client._

/**
 * SiestaClient is an abstract trait modelling an asynchronous client for a generic
 * RESTful API. SiestaClient is built on top of xxx
 *
 * Extending SiestaClient involves 
 * 
 * For a better understanding of RESTful web service clients, please see XXX
 */

// TODO: change contentType to a Spray variable
abstract trait NarcolepsyClient(
  val apiUri:       String,
  val contentType:  String = "application/xml") {

  // The return type for an API response.
  // Holds return code, either one representation or multiple, and a flag
  // indicating whether the representation is an error or not.
  // TODO: look at how squeryl deals with returning one row or multiple
  type RestfulResponse = (Int, Either[RestfulRepresentation, List[RestfulRepresentation]], Bool)

  // Simple synonym for the API parameters
  type RestfulParams = Map[String, String]

  // To store the content types (e.g. XML, JSON) supported by this RESTful web service
  val supportedContentTypes: List[String]

  // To store the parameters which are allowed for each resource type
  // TODO

  // TODO let's validate for supported contentTypes
  if (!validateContentType) {
    throw new RestfulClientException("Content type " + contentType + " is not supported")    
  }


  protected def validateContentType: Boolean

  protected def validateParams: Boolean

  // Debug
  Console.println("Accessing API at %s using %s".format(requestUri, contentType))

  // Build a Spray asynchronous http client with custom configuration options
  val client = new HttpClient(ClientConfig(
    requestTimeoutInMs = 300,
    userAgent = "orderly-scala-client v0.0.1",
    useRawUrl = false
  ))

  // TODO: let's add in the Accept header based on the contentType requested
  val request = new HttpRequest(
    method = HttpMethods.GET,
    uri = requestUri,
    headers = Nil,
    content = None,
    remoteHost = None,
    version = None
  )
  val future = client.dispatch(request)
  val response = future.get //()
  Console.println("Result of response: %s".format(response.toString))

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
      apiURL + resource +
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
}