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

  private val resources = new ArrayBuffer[Resource[_]]

  private val resourceMap = new HashMap[String, Class[_]]

  protected def resource[R](name: String, slug: String)(implicit manifestR: Manifest[R]): Resource[R] = {
    val typeR = manifestR.erasure.asInstanceOf[Class[R]]
    val r = new Resource[R](name, slug)
    addResource(r)
    addResourceMap(slug, typeR)
    r
  }

  private [narcolepsy] def addResource(r: Resource[_]) =
    resources.append(r)

  private [narcolepsy] def addResourceMap(slug: String, typeR: Class[_]) =
    resourceMap += ((slug, typeR))

  def getRepresentation(slug: String) =
    resourceMap get slug
}

// TODO: move this
class Resource[R](name: String, slug: String)