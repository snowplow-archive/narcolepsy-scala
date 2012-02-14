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

// Scala
import scala.collection.mutable.ArrayBuffer

// Narcolepsy
import marshallers.jackson.JacksonMarshaller
import marshallers.jaxb.JaxbMarshaller

/**
 * Representation is the parent trait for all representations handled by
 * NarcolepsyClient. A representation is REST speak for the instantiated form
 * of a REST resource. For the purposes of Narcolepsy, a Representation is a
 * Scala class that has been marshalled from XML/JSON/whatever by JAXB, Jackson
 * or similar.
 */
// TODO: I have added Jaxb and Jackson support back in via extends.
// TODO: In the future this should be decoupled (a Representation
// TODO: should be marshallable by any given Marshaller technology
// TODO: - this can be done by making an implicit conversion
// TODO: available at the correct point.
trait Representation extends JaxbMarshaller with JacksonMarshaller

/**
 * ErrorRepresentation is the parent trait for all representations which
 * represent some form of error object
 */
trait ErrorRepresentation extends Representation

/**
 * RepresentationWrapper is a subclass of Representation (to get the marshalling goodness),
 * designed to hold a list of individual Representations.
 */
trait RepresentationWrapper[R <: Representation] extends Representation {

  /**
   * Every RepresentationWrapper should set rtype equal to the same Representation class as R above
   */
  type rtype <: Representation

  /**
   * Every RepresentationWrapper should implement the toList method to
   * turn the RepresentationWrapper into a List[Representation] for
   * easier mapping/folding etc in Scala
   */
  def toList: List[R]
}
