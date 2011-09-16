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

// Scala
import scala.xml._

// Spray
import cc.spray._
import http._ // To get HttpRequest etc
import cc.spray.client._

// Orderly
import orderly.narcolepsy._
import representations._
import utils._

/**
 * NarcolepsyClient is an abstract class you can use to build an asynchronous client
 * for any well-behaved RESTful API. NarcolepsyClient is built on top of spray-client,
 * which in turn is a thin Scala wrapper around Ning's Async Http Client. (The
 * big difference between spray-client and Ning's AHC is that spray-client uses Akka
 * futures for the asynchronous funkiness.)
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
abstract class NarcolepsyClient(
  val rootUri:      String,
  val contentType:  String,
  val username:     String,
  val password:     String) { // TODO: change contentType to a Spray variable
                              // TODO: make a more general authentication input

  // -------------------------------------------------------------------------------------------------------------------
  // Alternative constructors
  // -------------------------------------------------------------------------------------------------------------------

  // TODO: handle defaultRootUri

  // TODO: handle default content type

  // -------------------------------------------------------------------------------------------------------------------
  // Need to populate the below vals to define a new NarcolepsyClient
  // -------------------------------------------------------------------------------------------------------------------

  // To provide a human readable name for this client, e.g. "Shopify Scala client"
  val clientName: String

  // Define the software version, e.g. 1.1.0 or 2
  val clientVersion: RestfulVersion

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
  val versionHeader: Option[String]

  // The default root API URL if supplied. Only makes sense for APIs from SaaS companies with fixed API endpoints
  // Set to None if a default root API does not make sense for this API
  val defaultRootUri: Option[String]

  // To track which parameters are valid for GETs
  // val

  // val validGetParams: Map[String, String] // TODO: think need a different way to represent this

  // The minimum version of the RESTful API supported. SoftwareVersion taken from Maven versioning
  // TODO: implement all this (quite bespoke per API?)
  val minVersionSupported: RestfulVersion
  val maxVersionSupported: RestfulVersion

  // -------------------------------------------------------------------------------------------------------------------
  // Validation to check that the constructor arguments are okay
  // -------------------------------------------------------------------------------------------------------------------

  // First let's validate that we have a rootUri
  Option(rootUri).getOrElse(throw new NarcolepsyConfigurationException("rootUri missing, must be set for %s".format(clientName)))

  // Now let's validate that the content type passed in is legitimate for this API
  if (!(supportedContentTypes contains contentType)) {
    throw new NarcolepsyConfigurationException("Content type " + contentType + " is not supported")
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

  // -------------------------------------------------------------------------------------------------------------------
  // Utility methods to support the GET, POST etc methods
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
      uri = requestUri,
      headers = Nil,
      content = None, // TODO: this needs to be configurable
      remoteHost = None, // TODO: what is this?
      version = None // TODO: what is this?
    )
    val future = client.dispatch(request)
    val response = future.get // Block TODO: make this async capable

     // Return the RestfulResponse
    (200, Left(response.toString()), false) // TODO: populate with proper values
  }

  // TODO: about the below: don't assume XML returned (might be JSON)
  // TODO: need to think about how the validation will work (taking it out for now)
}