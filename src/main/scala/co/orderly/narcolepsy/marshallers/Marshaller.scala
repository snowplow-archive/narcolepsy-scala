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
package marshallers

/**
 * Parent marshalling trait, extended by any marshaller
 */
sealed trait Marshaller

/**
 * A MultiMarshaller can choose between different marshallers based on
 * the supplied content type.
 */
trait MultiMarshaller extends Marshaller {

  /**
   * Abstract method to marshal a given representation into a string,
   * based on the supplied content type.
   */
  def fromRepresentation[R <: Representation](contentType: String, representation: R): String
}

/**
 * A ContentTypeMarshaller should be extended by any single-format marshaller
 * which can only handle one incoming content type.
 *
 * ContentTypeMarshaller extends MultiMarshaller so it can be used in places
 * where a MultiMarshaller is expected (e.g. assigning a marshaller to a
 * Narcolepsy Client).
 */
trait ContentTypeMarshaller extends MultiMarshaller {

  /**
   * Take the contentType argument and discard it
   */
  def fromRepresentation[R <: Representation](contentType: String, representation: R): String =
    fromRepresentation(representation)

  /**
   * Abstract method to marshal a given representation into a string
   */
  def fromRepresentation[R <: Representation](representation: R): String
}