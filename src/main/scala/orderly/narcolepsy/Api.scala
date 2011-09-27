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
import collection.mutable.HashMap

/**
 * Api allows you to define a mapping of RESTful resource names (e.g. "products")
 * to RESTful Representations (e.g. Product) and RepresentationLists (e.g. ProductList).
 *
 * Api is heavily inspired by Squeryl's Schema approach, see for example:
 * https://github.com/max-l/Squeryl/blob/master/src/main/scala/org/squeryl/Schema.scala
 */
trait Api {

  /**
   * When extending Api, call resource to define individual resources within the Api e.g:
   * val products = resource[Product, ProductList]("products")
   * @param slug The URL slug identifying the resource, e.g. "products"
   * @return The instantiated Resource
   */
  protected def resource[R <: Representation, RW <: RepresentationWrapper](slug: String)(implicit manifestR: Manifest[R], manifestRW: Manifest[RW]): Resource[R, RW] = {
    val typeR = manifestR.erasure.asInstanceOf[Class[R]]
    val typeRW = manifestRW.erasure.asInstanceOf[Class[RW]]
    new Resource[R, RW](slug, typeR, typeRW) // Return the new Resource
  }
}

/**
 * Flags an exception in the configuration of the API - e.g. when there is a request
 * for a slug which hasn't been defined
 */
class ApiConfigurationException(message: String = "") extends RuntimeException(message) {
}