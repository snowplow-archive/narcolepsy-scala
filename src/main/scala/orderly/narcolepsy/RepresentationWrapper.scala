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
}

/**
 * Representation is the parent class for all representations handled by
 * NarcolepsyClient. A representation is REST speak for the instantiated form
 * of a REST resource. For the purposes of Narcolepsy, a Representation is a
 * Scala class that has been marshalled from XML/JSON/whatever by JAXB, Jackson
 * or similar.
 */
abstract class RepresentationWrapper extends Listable[Representation]