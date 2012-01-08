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
import collection.mutable.ArrayBuffer

/**
 * Api allows you to define a mapping of RESTful resource names (e.g. "products")
 * to RESTful Representations (e.g. Product) and RepresentationLists (e.g. ProductList).
 *
 * Api is heavily inspired by Squeryl's Schema approach, see for example:
 * https://github.com/max-l/Squeryl/blob/master/src/main/scala/org/squeryl/Schema.scala
 */
trait Api {

  // -------------------------------------------------------------------------------------------------------------------
  // Resource-handling logic
  // -------------------------------------------------------------------------------------------------------------------

  // Private mutable array to hold the resources defined so far
  private val resources = new ArrayBuffer[Resource[_, _]]

  /**
   * When extending Api, call resource to define individual resources within the Api e.g:
   * val products = resource[Product, ProductList]("products")
   * @param slug The URL slug identifying the resource, e.g. "products"
   * @return The instantiated Resource
   */
  protected def resource[R <: Representation, RW <: RepresentationWrapper[_ <: Representation]](slug: String)(implicit manifestR: Manifest[R], manifestRW: Manifest[RW]): Resource[R, RW] = {
    val typeR = manifestR.erasure.asInstanceOf[Class[R]]
    val typeRW = manifestRW.erasure.asInstanceOf[Class[RW]]
    val r = new Resource[R, RW](slug, typeR, typeRW) // Return the new Resource
    addResource(r)
    r
  }

  /**
   * Helper to add a resource into the resource array
   * @param r The resource to add into the resource array
   */
  protected [narcolepsy] def addResource(r: Resource[_, _]) =
    resources.append(r)

  // -------------------------------------------------------------------------------------------------------------------
  // Client-handling logic
  // -------------------------------------------------------------------------------------------------------------------

  // Private var to hold the client used to access this resource
  private var client: Client = _

  /**
   * Use this to attach an API client to each resource currently defined within the
   * Api. Note that the Resource class's attachClient can be called directly to
   * attach a specific API client to an individual resource type.
   * @param client The API client to attach to each resource
   */
  def attachClient(client: Client) {

    // First attach to the API
    this.client = client

    // Now attach to each Resource defined with this API
    resources.foreach(_.attachClient(client))
  }
}

/**
 * Flags an exception in the configuration of the API - e.g. when there is a request
 * for a slug which hasn't been defined
 */
class ApiConfigurationException(message: String = "") extends RuntimeException(message)