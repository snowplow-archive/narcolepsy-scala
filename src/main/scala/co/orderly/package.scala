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
package co.orderly

// Narcolepsy
import narcolepsy.{Representation, RepresentationWrapper, RestfulError}

/**
 * Core Narcolepsy types for working with REST. They are always available without an explicit export.
 * This approach was taken following discussion on Stack Overflow here:
 * http://stackoverflow.com/questions/7441277/scala-type-keyword-how-best-to-use-it-across-multiple-classes
 * All types defined here start with "Restful" to make them more obvious throughout the code
 */
package object narcolepsy {

  // -------------------------------------------------------------------------------------------------------------------
  // RESTful package constants
  // -------------------------------------------------------------------------------------------------------------------

  // Default character set is UTF8
  val DefaultEncoding: String = HTTP.UTF_8

  // -------------------------------------------------------------------------------------------------------------------
  // Core RESTful types
  // -------------------------------------------------------------------------------------------------------------------

  // Simple synonym for the API parameters
  type RestfulParams = Map[String, String]

  // For an HTTP status code
  type RestfulStatusCode = Int

  // Simple synonym for HTTP headers
  type RestfulHeaders = Map[String, String]

  // For an HTTP body
  type RestfulBody = Option[String]

  // -------------------------------------------------------------------------------------------------------------------
  // API response types
  // -------------------------------------------------------------------------------------------------------------------

  // Raw response (prior to massaging into one of the below types)
  // 1. HTTP status code
  // 2. HTTP response headers
  // 3. HTTP response body, or None
  type RestfulResponse = (RestfulStatusCode, RestfulHeaders, RestfulBody)

  // Unmarshalled response from a request - either:
  // 1. An RestfulError class typed with E ErrorRepresentation, or:
  // 2. An R Representation, or None
  type UnmarshalledResponse[E <: ErrorRepresentation, R <: Representation] = Either[RestfulError[E], Option[R]]
}