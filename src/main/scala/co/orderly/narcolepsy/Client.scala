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

// Orderly
import orderly.narcolepsy._
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
abstract class Client(
  val rootUri:      Option[String],
  val contentType:  Option[String],
  val username:     String,
  val password:     String) extends HttpAdapter {

  // TODO: let's use the Cake pattern to decouple all of this from the HttpClient implementation
  // TODO: http://jonasboner.com/2008/10/06/real-world-scala-dependency-injection-di.html

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
  // Set additional variables
  // -------------------------------------------------------------------------------------------------------------------

  // How the client should identify itself to the RESTful API
  val userAgent = "%s/%s [NarcolepsyClient]".format(clientName, clientVersion)
}

/**
 * Flags an exception in the configuration of a client - i.e. the subclassing of the
 * Client abstract class above
 */
class ClientConfigurationException(message: String = "") extends RuntimeException(message) {
}