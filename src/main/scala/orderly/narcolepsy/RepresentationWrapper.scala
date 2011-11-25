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
import scala.collection.mutable.ArrayBuffer

// As per http://stackoverflow.com/questions/7666759/can-i-use-a-type-bound-on-a-scala-abstract-method-and-then-tighten-up-the-defin
trait Listable[+R <: Representation] {

  /**
   * Every Wrapper should implement the toList method to turn the
   * RepresentationWrapper into a List[Representation] for easier
   * mapping/folding etc in Scala
   * Weirdly I need to write [R <: etc below rather than just
   * List[Representation] because otherwise I get a compiler
   * error where I call toList on account of this problem:
   * http://scala-programming-language.1934581.n4.nabble.com/Surprising-type-mismatch-with-generics-td2257197.html
   */
  def toList: List[R]

  // http://stackoverflow.com/questions/663254/scala-covariance-contravariance-question
  // def fromList[L >: R](representations: List[L])

  // And bear with me again <sorry>
  protected def arrayBufferFromList[L <: Representation](list: List[L]): ArrayBuffer[L] = {
    val ab = new ArrayBuffer[L]
    ab ++= list
    ab
  }
}

/**
 * RepresentationWrapper is a subclass of Representation (to get the marshalling goodness),
 * designed to hold a list of individual Representations.
 */
trait RepresentationWrapper extends Representation with Listable[Representation]