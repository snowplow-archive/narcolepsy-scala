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
package co.orderly.narcolepsy

// TODO: add imports for version capture

// Orderly
import utils._
import adapters._

/**
 * Client is an abstract Narcolepsy class you can use to build a web services client
 * for any well-behaved RESTful API. The Narcolepsy Client is designed to be agnostic of
 * the underlying HTTP library you use. The initial version comes bundled with an adapter
 * for the Apache HttpClient module; an adapter for spray-client is also in the works.
 *
 * The Narcolepsy Client provides a higher-level of abstraction than that typically found in a
 * RESTful API toolkit like spray-client or Apache HttpClient. Creating an API-specific client
 * using Narcolepsy should be as simple as providing some configuration variables, generating
 * the JAXB representation definitions and mapping those representations to resource slugs.
 *
 * Narcolepsy is licensed under the Apache License, Version 2.0.
 *
 * For a better understanding of RESTful web service clients, please read XXX
 *
 * For more on Narcolepsy see the GitHub project: https://github.com/orderly/narcolepsy
 */
absract class Client(
  val rootUri:      Option[String],
  val contentType:  Option[String],
  val username:     String,
  val password:     String) extends HttpAdapter {

  // -------------------------------------------------------------------------------------------------------------------
  // Need to populate the below vals to define a new NarcolepsyClient
  // -------------------------------------------------------------------------------------------------------------------

  // To provide a human readable name for this client, e.g. "PrestaShop Scala client"
  protected def clientName: String

  // To store the content types (e.g. XML, JSON) supported by this RESTful web service
  protected val supportedContentTypes: List[String]

  // Map resource slug names against the Representation subclasses required by this RESTful API
  protected val apiResources: Api

  // -------------------------------------------------------------------------------------------------------------------
  // You can override the following defaults in your NarcolepsyClient if you want
  // -------------------------------------------------------------------------------------------------------------------

  // The default root API URL if supplied. Only makes sense for APIs for hosted services with fixed API endpoints
  // Set to None if a default root API does not make sense for this API
  protected val defaultRootUri: Option[String] = None

  // The default content type if none is supplied
  protected val defaultContentType: Option[String] = None

  // Define the characterset to use (e.g. UTF-8)
  protected val encoding: String = DefaultEncoding

  // -------------------------------------------------------------------------------------------------------------------
  // Calculated / automatically detected variables
  // -------------------------------------------------------------------------------------------------------------------

  // Define the software version, e.g. 1.1.0 or 3.0 m2
  private val clientVersion = new BufferedReader(new InputStreamReader(getClass.getResourceAsStream("/version"))).readLine()

  // How the client should identify itself to the RESTful API
  private val userAgent = "%s/%s [NarcolepsyClient]".format(clientName, clientVersion)

  // -------------------------------------------------------------------------------------------------------------------
  // Validation to check that the constructor arguments are okay
  // -------------------------------------------------------------------------------------------------------------------

  // Check we have a rootUri and add a trailing slash if necessary
  private val trailSlash = (uri: String) => if (uri.matches(".*/")) uri else (uri + "/")
  private val apiUri = trailSlash((rootUri, defaultRootUri) match {
    case (Some(uri), _) => uri
    case (None, Some(uri)) => uri
    case _ => throw new ClientConfigurationException("No rootUri or defaultRootUri provided")
  })

  // Check that we have a content type
  private val apiContentType = (contentType, defaultContentType, supportedContentTypes) match {
    case (Some(ct), _, _) => ct // If we have a contentType passed in, use that
    case (None, Some(ct), _) => ct // Else if we have a default content type, use that
    case (_, _, ct :: Nil) => ct /// FInally grab the only support type that is set
    case _ => throw new ClientConfigurationException("No contentType, single supportedContentTYpes or defaultContentType provided")
  }

  // Now let's validate that the content type passed in is legitimate for this API
  if (!(supportedContentTypes contains apiContentType)) {
    throw new ClientConfigurationException("Content type " + apiContentType + " is not supported")
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Finally we build a 'clean' configuration object for use in an HttpAdapter
  // -------------------------------------------------------------------------------------------------------------------

  object Configuration {
    val name = clientName
    val version = clientVersion
    val apiUri = this.apiUri
    val apiContentType = this.apiContentType
    val encoding = this.encoding
  }

}

/**
 * Flags an exception in the configuration of a client - i.e. the subclassing of the
 * Client abstract class above
 */
class ClientConfigurationException(message: String = "") extends RuntimeException(message)