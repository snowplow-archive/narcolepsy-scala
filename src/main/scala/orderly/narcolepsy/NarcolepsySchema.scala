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
package orderly.narcolepsy

// Scala
import collection.mutable.{HashMap, ArrayBuffer}

/**
 * NarcolepsySchema allows you to define a mapping of RESTful resource names
 * (e.g. "products") to RESTful Representations (e.g. Product)
 *
 * NarcolepsySchema is heavily inspired by Squeryl's schema approach, see:
 * https://github.com/max-l/Squeryl/blob/master/src/main/scala/org/squeryl/Schema.scala
 */
trait NarcolepsySchema {

  private val representations = new ArrayBuffer[Representation[_]]

  private val representationTypes = new HashMap[Class[_], Representation[_]]

  protected def representation[R](name: String)(implicit manifestR: Manifest[R]): Representation[R] = {
    val typeR = manifestR.erasure.asInstanceOf[Class[R]]
    val r = new Table[T](name, typeR, this, None)
    addRepresentation(r)
    addRepresentationType(typeR, r)
    r
  }

  private [squeryl] def addRepresentation(r: Representation[_]) =
    representations.append(r)

  private [squeryl] def addRepresentationType(typeR: Class[_], r: Representation[_]) =
    representationTypes += ((typeR, r))
}