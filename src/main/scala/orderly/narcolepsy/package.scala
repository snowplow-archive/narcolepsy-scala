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

// Let's import Maven versioning (we'll give it a friendly synonym below)
import org.apache.maven.artifact.versioning.DefaultArtifactVersion

// Need to import the Narcolepsy Representation
import orderly.narcolepsy.Representation

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

  // Response from a GET request
  type GetResponse[R] = (Int, Either[R, List[R]], Boolean)

  // Response from a DELETE request
  type DeleteResponse[R] = (Int, Option[R], Boolean)

  // -------------------------------------------------------------------------------------------------------------------
  // Miscellaneous types
  // -------------------------------------------------------------------------------------------------------------------

  // Simple synonym for the API parameters
  type RestfulParams = Map[String, String]

  // To store a version of REST client or server
  type RestfulVersion = DefaultArtifactVersion


}