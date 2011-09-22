/*
 * Copyright (c) 2011 Orderly Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package orderly.narcolepsy

// Java
import java.io.StringReader

// JAXB and XML
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBElement
import javax.xml.transform.stream.StreamSource

// Spray
import cc.spray._
import http._ // To get HttpRequest etc
import client._

// Orderly
import orderly.narcolepsy._
import representations._
import utils._

/**
 * Client is an abstract Narcolepsy class you can use to build an asynchronous client
 * for any well-behaved RESTful API. Client is built on top of spray-client, which in
 * turn is a thin Scala wrapper around Ning's Async Http Client. (The big difference
 * between spray-client and Ning's AHC is that spray-client uses Akka futures for the
 * asynchronous funkiness.)
 *
 * spray-client API docs: http://spray.github.com/spray/api/spray-client/index.html
 * Async Http Client docs: http://sonatype.github.com/async-http-client/apidocs/index.html
 *
 * NarcolepsyClient provides a higher-level of abstraction than that typically found in a
 * RESTful API toolkit like spray-client or AHC. Creating an API-specific client using
 * Narcolepsy is as simple as providing some configuration variables, generating the JAXB
 * representation definitions and mapping those representations to resource slugs.
 *
 * NarcolepsyClient is licensed under the Apache License, Version 2.0 - the same
 * as spray-client and the Async Http Client.
 *
 * For a better understanding of RESTful web service clients, please read XXX
 *
 * For more on Narcolepsy see the GitHub project: https://github.com/orderly/narcolepsy
 */
abstract class Client(
  val rootUri:      Option[String],
  val contentType:  Option[String],
  val username:     String,
  val password:     String) { // TODO: change contentType to a Spray variable
                              // TODO: make a more general authentication input

  // -------------------------------------------------------------------------------------------------------------------
  // Need to populate the below vals to define a new NarcolepsyClient
  // -------------------------------------------------------------------------------------------------------------------

  // To provide a human readable name for this client, e.g. "Shopify Scala client"
  val clientName: String

  // Define the software version, e.g. 1.1.0 or 2
  val clientVersion: RestfulVersion

  // Define the format that errors are returned in
  // Valid formats are plaintext, representation or mixed
  val errorFormat: ErrorFormat

  // To store the content types (e.g. XML, JSON) supported by this RESTful web service
  val supportedContentTypes: List[String] // TODO: change contentType to a Spray variable

  // The default content type if none is supplied
  val defaultContentType: Option[String] // TODO: change contentType to a Spray variable

  // The header variable which contains the version information
  // Set to None if there is no easily available version information in a header
  val versionHeader: Option[String]

  // The default root API URL if supplied. Only makes sense for APIs from SaaS companies with fixed API endpoints
  // Set to None if a default root API does not make sense for this API
  val defaultRootUri: Option[String]

  // Map resource slug names against the Representation subclasses required by this RESTful API
  val apiResources: Api

  // The minimum version of the RESTful API supported. RestfulVersion taken from Maven versioning
  // TODO: implement all this (quite bespoke per API?)
  val minVersionSupported: Option[RestfulVersion]
  val maxVersionSupported: Option[RestfulVersion]

  // -------------------------------------------------------------------------------------------------------------------
  // Validation to check that the constructor arguments are okay
  // -------------------------------------------------------------------------------------------------------------------

  // Check we have a rootUri and add a trailing slash if necessary
  val trailSlash = (uri: String) => if (uri.matches(".*/")) uri else (uri + "/")
  val apiUri = trailSlash((rootUri, defaultRootUri) match {
    case (Some(uri), _) => uri
    case (None, Some(uri)) => uri
    case _ => throw new ClientConfigurationException("No rootUri or defaultRootUri provided")
  })

  // Check that we have a content type
  val apiContentType = (contentType, defaultContentType) match {
    case (Some(ct), _) => ct
    case (None, Some(ct)) => ct
    case _ => throw new ClientConfigurationException("No contentType or defaultContentType provided")
  }

  // Now let's validate that the content type passed in is legitimate for this API
  if (!(supportedContentTypes contains apiContentType)) {
    throw new ClientConfigurationException("Content type " + apiContentType + " is not supported")
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Actual HTTP client constructor
  // -------------------------------------------------------------------------------------------------------------------

  // Finally build an asynchronous spray-client with custom configuration options
  val client = new HttpClient(ClientConfig(
    requestTimeoutInMs = 500,
    userAgent = "%s/%s [NarcolepsyClient]".format(clientName, clientVersion),
    useRawUrl = false
  ))

  // -------------------------------------------------------------------------------------------------------------------
  // The actual execute method which runs the GET, POST etc methods
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * Handles an HTTP request to the web service.
   * @param method HttpMethod to apply to this request
   * @param requestUri Relative path to resource. Attach rootUri to get the absolute URI
   * @return A RestfulResponse object
   */
  protected def execute(
    resource: String,
    requestMethod: HttpMethod,
    requestUri: String): RestfulResponse = {

    // TODO: let's add in the Accept header based on the contentType requested
    val request = new HttpRequest(
      method = requestMethod,
      uri = apiUri + requestUri,
      headers = Nil,
      content = None, // TODO: this needs to be configurable
      remoteHost = None, // TODO: what is this?
      version = None // TODO: what is this?
    )
    val future = client.dispatch(request)
    val responseString = scala.io.Source.fromInputStream(future.get.content.get.inputStream).mkString("") // Blocking TODO: make this async capable

    Console.println(">>>>>>>>>>" + responseString + "<<<<<<<<")

    // TODO: check the return code
    // If return code != 200, then we need to go into error handling mode

    // Okay we now have some text, so next we need to turn it into a representation
    // List<Product> output = new Vector<Product>();
    val representationClass = apiResources.representationNameFromSlug(resource)
    val jaxbContext = JAXBContext.newInstance(representationClass)
    val unmarshaller = jaxbContext.createUnmarshaller()
    val root = unmarshaller.unmarshal(new StreamSource(new StringReader(responseString)), representationClass)

    // TODO: next I need to update the Api definition so it contains the plural form (ProductList) as well as the
    // singular form

    import orderly.mdm.representations.Product
    import orderly.mdm.representations.wrappers.ProductList
    import scalaj.collection.Imports._
    val r = root.getValue().asInstanceOf[ProductList]
    val representations = (r.getProducts).asScala.toList // TODO: obviously this is not very clever

     // Return the RestfulResponse
    (200, Right(representations), false) // TODO: populate with proper values
  }

  // -------------------------------------------------------------------------------------------------------------------
  // GET verb methods
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * Retrieve (GET) a resource, self-assembly version without parameters
   * @param resource Type of resource to retrieve
   * @return RESTful response from the API
   */
  def get(resource: String): RestfulResponse = {
    get(resource, None, None)
  }

  /**
   * Retrieve (GET) a resource, self-assembly version with parameters
   * @param resource Type of resource to retrieve
   * @param params Map of parameters (one or more of 'filter', 'display', 'sort', 'limit')
   * @return RESTful response from the API
   */
  def get(resource: String, params: RestfulParams): RestfulResponse = {
    get(resource, None, Some(params))
  }

  /**
   * Retrieve (GET) a resource, self-assembly version without parameters
   * @param resource Type of resource to retrieve
   * @param id Resource ID to retrieve
   * @return RESTful response from the API
   */
  def get(resource: String, id: Int): RestfulResponse = {
    get(resource, Some(id), None)
  }

  /**
   * Retrieve (GET) a resource, self-assembly version with parameters
   * @param resource Type of resource to retrieve
   * @param id Resource ID to retrieve
   * @param params Map of parameters (one or more of 'filter', 'display', 'sort', 'limit')
   * @return RESTful response from the API
   */
  def get(resource: String, id: Int, params: RestfulParams): RestfulResponse = {
    get(resource, Some(id), Some(params))
  }

  /**
   * Retrieve (GET) a resource, helper version using Options
   * @param resource Type of resource to retrieve
   * @param id Optional resource ID to retrieve
   * @param params Optional Map of parameters (one or more of 'filter', 'display', 'sort', 'limit')
   * @return RESTful response from the API
   */
  protected def get(resource: String, id: Option[Int], params: Option[RestfulParams]): RestfulResponse = {
    getURL(resource,
      resource +
      (if (id.isDefined) "/%d".format(id.get) else "") +
      (if (params.isDefined) "?%s".format(RestfulHelpers.canonicalize(params.get)) else "")
    )
  }

  /**
   * Retrieve (GET) a resource, URL version
   * @param resource The type of resource to retrieve
   * @param uri A URL which explicitly sets the resource type, ID(s) and parameters to retrieve
   * @return RESTful response from the API
   */
  def getURL(resource: String, uri: String): RestfulResponse = {
    execute(resource, HttpMethods.GET, uri) // Execute the API call using GET
  }

  // TODO: about the below: don't assume XML returned (might be JSON)
  // TODO: need to think about how the validation will work (taking it out for now)
}

/**
 * Flags an exception in the configuration of a client - i.e. the extending of the
 * abstract Client class above
 */
class ClientConfigurationException(message: String = "") extends RuntimeException(message) {
}