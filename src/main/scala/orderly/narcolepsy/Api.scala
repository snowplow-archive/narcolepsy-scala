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
 * Api allows you to define a mapping of RESTful resource names (e.g. "products")
 * to RESTful Representations (e.g. Product)
 *
 * Api is heavily inspired by Squeryl's Schema approach, see for example:
 * https://github.com/max-l/Squeryl/blob/master/src/main/scala/org/squeryl/Schema.scala
 */
trait Api {

  private val resourceMap = new HashMap[String, Class[_ <: Representation]]
  private val resourceListMap = new HashMap[String, Class[_ <: Representation]]

  protected def resource[R <: Representation, RL <: Representation](slug: String)(implicit manifestR: Manifest[R], manifestRL: Manifest[RL]): Resource[R, RL] = {
    val typeR = manifestR.erasure.asInstanceOf[Class[R]]
    val typeRL = manifestRL.erasure.asInstanceOf[Class[RL]]
    addResourceMap(slug, typeR)
    addResourceListMap(slug, typeRL)
    new Resource[R, RL](slug)
  }

  private [narcolepsy] def addResourceMap(slug: String, typeR: Class[_ <: Representation]) =
    resourceMap += ((slug, typeR))

  private [narcolepsy] def addResourceListMap(slug: String, typeRL: Class[_ <: Representation]) =
    resourceListMap += ((slug, typeRL))

  def representationNameFromSlug(slug: String) =
    (resourceListMap get slug).getOrElse(throw new ApiConfigurationException("No representation found for slug %s".format(slug)))
}

/**
 * Flags an exception in the configuration of the API - e.g. when there is a request
 * for a slug which hasn't been defined
 */
class ApiConfigurationException(message: String = "") extends RuntimeException(message) {
}