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
package orderly.narcolepsy.utils

/**
 * Wrapper object for the type synonyms which are used by Narcolepsy.
 * We only have to create this object at all because the type keyword
 * in Scala isn't a first-class citizen like it is in Haskell. See
 * discussion of this "issue" on Stack Overflow here:
 *
 * xxx TODO add URL
 *
 * For brevity this is imported in the places that use it like so:
 * import orderly.narcolepsy.utils.{RestfulTypes => RT}
 */
object RestfulTypes {

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
  type RestfulResourceMap = Map[String, RestfulRepresentation]
}