/*
 * Copyright (c) 2012 Orderly Ltd. All rights reserved.
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
 * YOUR representation definitions and mapping those representations to resource slugs.
 *
 * Narcolepsy is licensed under the Apache License, Version 2.0.
 *
 * For a better understanding of RESTful web service clients, please read XXX
 *
 * For more on Narcolepsy see the GitHub project: https://github.com/orderly/narcolepsy-scala
 */
abstract class Client(
  val rootUri:      Option[String],
  val contentType:  Option[String],
  val username:     String,
  val password:     String) extends HttpAdapter {

  // -------------------------------------------------------------------------------------------------------------------
  // Need to populate the below vals to define a new NarcolepsyClient
  // -------------------------------------------------------------------------------------------------------------------

  // To provide a human readable name for this client, e.g. "PrestaShop Scala client"
  protected def name: String

  // Define the version of this client, e.g. 1.1.0 or 3.0 m2
  protected def version: String

  // To store the content types (e.g. XML, JSON) supported by this RESTful web service
  protected val contentTypes: List[String]

  // Map resource slug names against the Representation subclasses required by this RESTful API
  protected val resources: Api

  // -------------------------------------------------------------------------------------------------------------------
  // You can override the following defaults in your NarcolepsyClient if you want
  // -------------------------------------------------------------------------------------------------------------------

  // The default root API URL if supplied. Only makes sense for APIs for hosted services with fixed API endpoints
  // Set to None if a default root API does not make sense for this API, or if supportedContentTypes
  // is
  protected val defaultRootUri: Option[String] = None

  // The default content type if none is supplied
  protected val defaultContentType: Option[String] = None

  // Define the characterset to use (e.g. UTF-8)
  protected val encoding: String = DefaultEncoding

  // -------------------------------------------------------------------------------------------------------------------
  // Calculated / automatically detected variables
  // -------------------------------------------------------------------------------------------------------------------

  // How the client should identify itself to the RESTful API
  private val _userAgent = "%s/%s [NarcolepsyClient]".format(name, version)

  // -------------------------------------------------------------------------------------------------------------------
  // Validation to check that the constructor arguments are okay
  // -------------------------------------------------------------------------------------------------------------------

  // Check we have a rootUri and add a trailing slash if necessary
  private val trailSlash = (uri: String) => if (uri.matches(".*/")) uri else (uri + "/")
  private val _rootUri = trailSlash((rootUri, defaultRootUri) match {
    case (Some(uri), _) => uri    // rootUri takes precedence
    case (None, Some(uri)) => uri // defaultRootUri comes second
    case _ => throw new ClientConfigurationException("No rootUri or defaultRootUri provided")
  })

  // Check that we have a content type
  private val _contentType = (contentType, defaultContentType, contentTypes) match {
    case (Some(ct), _, _) => ct    // By default if we have a contentType passed in, use that
    case (None, Some(ct), _) => ct // Else if we have a default content type, use that
    case (_, _, ct :: Nil) => ct   // Finally if supportedContentTypes is a list with one element, grab that
    case _ => throw new ClientConfigurationException("Cannot determine content type to use - please set contentType, defaultContentType or define a one-element contentTypes list")
  }

  // Now let's validate that the content type is legitimate for this API
  if (!(contentTypes contains _contentType)) {
    throw new ClientConfigurationException("Content type " + _contentType + " is not supported")
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Finally we build a 'clean' configuration object for use in an HttpAdapter
  // -------------------------------------------------------------------------------------------------------------------

  val configuration = ClientConfiguration(
    name = name,
    version = version,
    encoding = encoding,
    username = username,
    password = password,
    userAgent = _userAgent,
    rootUri = _rootUri,
    contentType = _contentType
    )
}

/**
 * To store a complete configuration for a client. Used by the HttpAdapters
 */
case class ClientConfiguration(name: String,
  encoding: String,
  version: String,
  username: String,
  password: String,
  userAgent: String,
  rootUri: String,
  contentType: String
  )

/**
 * Flags an exception in the configuration of a client - i.e. the subclassing of the
 * Client abstract class above
 */
class ClientConfigurationException(message: String = "") extends RuntimeException(message)