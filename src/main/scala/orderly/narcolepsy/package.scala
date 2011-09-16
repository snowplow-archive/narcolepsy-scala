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

// TODO: update this soon
// import orderly.representations.RestfulRepresentation

/**
 * Core Narcolepsy types for working with REST. They are always available without an explicit export.
 * This approach was taken following discussion on Stack Overflow here:
 * http://stackoverflow.com/questions/7441277/scala-type-keyword-how-best-to-use-it-across-multiple-classes
 */
package object narcolepsy {

  // TODO: remove this. Temporary to test the API client without fannying around with JAXB
  type RestfulRepresentation = String

  // The return type for an API response.
  // Holds return code, either one representation or multiple, and a flag
  // indicating whether the representation is an error or not.
  // TODO: look at how squeryl deals with returning one row or multiple
  // TODO: are we definitely returning a List of RestfulRepresentations?
  type RestfulResponse = (Int, Either[RestfulRepresentation, List[RestfulRepresentation]], Boolean)

  // Simple synonym for the API parameters
  type RestfulParams = Map[String, String]

  // To store the map of resources supported by a given implementation of NarcolepsyClient
  // Format is:
  //      TODO
  type RestfulResourceMap = Map[String, Tuple2[RestfulRepresentation, List[String]]]
}