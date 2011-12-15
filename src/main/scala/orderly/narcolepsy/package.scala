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
package orderly

// Java
import java.text.SimpleDateFormat

// Maven versioning (we'll give it a friendly synonym below)
import org.apache.maven.artifact.versioning.DefaultArtifactVersion

// Narcolepsy
import orderly.narcolepsy.{Representation, RepresentationWrapper}

/**
 * Core Narcolepsy types for working with REST. They are always available without an explicit export.
 * This approach was taken following discussion on Stack Overflow here:
 * http://stackoverflow.com/questions/7441277/scala-type-keyword-how-best-to-use-it-across-multiple-classes
 * All types defined here start with "Restful" to make them more obvious throughout the code
 */
package object narcolepsy {

  // -------------------------------------------------------------------------------------------------------------------
  // API response types
  // -------------------------------------------------------------------------------------------------------------------

  // Raw response (prior to massaging into one of the below types)
  // 1. Return code
  // 2. Response headers
  // 2. Response body (Option)
  type RestfulResponse = (Int, List[String], Option[String])

  // Response from a GET request:
  // 1. Return code
  // 2. Either a subclass of Representation or a List of the same
  // 3. Is the Representation an error Representation?
  type GetResponse[R] = (Int, Either[R, List[R]], Boolean)

  // Response from a DELETE request
  // 1. Return code
  // 2. Either None (no news is good news) or a subclass of Representation
  // 3. Is the Representation an error Representation?
  type DeleteResponse[R] = (Int, Option[R], Boolean)

  // -------------------------------------------------------------------------------------------------------------------
  // Miscellaneous types
  // -------------------------------------------------------------------------------------------------------------------

  // Simple synonym for the API parameters
  type RestfulParams = Map[String, String]

  // To identify a REST server/client version
  type RestfulVersion = DefaultArtifactVersion

  // -------------------------------------------------------------------------------------------------------------------
  // Helper methods
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * Whether or not to add a root key aka "top level segment" when (un)marshalling JSON, as
   * as per http://stackoverflow.com/questions/5728276/jackson-json-top-level-segment-inclusion
   */
  def needRootKey(obj: Any) = obj match {
    case o:RepresentationWrapper => false // Don't include as we get the root key for free with a wrapper
    case _ => true                        // Yes include a root key
  }

  /**
   * Standardise the date format to use for (un)marshalling
   */
  def getDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
}