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
package co.orderly.narcolepsy

// Java
import java.util.UUID

abstract class Query(resource: String) {

  protected var payload: Option[String] = None // Payload trait allows building this

  protected var id: Option[String] = None // Id trait allows building this

  protected var params: Option[RestfulParams] = None // Parameter name-value pairs are squirted into the querystring

  protected var print: Boolean = false

  protected var slug: String = resource

  def debugPrint(): this.type = {
    this.print = true
    this
  }

  def overrideSlug(slug: String): this.type = {
    this.slug = slug
    this
  }

  def run(): RestfulResponse = {

    val uri = (this.slug +
      (if (id.isDefined) "/%s".format(id.get) else "") +
      (if (params.isDefined) "?%s".format(canonicalize(params.get)) else "")
      )
  }

  /**
   * Returns a canonicalized, escaped string of &key=value pairs from a Map of parameters
   * @param params A map of parameters ('filter', 'display' etc)
   * @return A canonicalized escaped string of the parameters
   */
  protected def canonicalize(params: RestfulParams): String = {

    val nameValues = params.map { param => new BasicNameValuePair(param._1, param._2) }
    URLEncodedUtils.format(nameValues.toSeq.asJava, CHARSET)
  }

  // TODO: add option to throw an exception if a non-success error code retrieved

  /*
  def get(tpe: String, id: Int): QueryResult[RestfulResponse] =
    new QueryResult[RestfulResponse] {
       def run(): RestfulResponse = null // code to make rest call goes here
    } */
}

trait Payload extends Query {

  def setPayload(payload: String): this.type = {
    this.payload = Option(payload)
    this
  }
}

trait Id extends Query {

  def setId(id: String): this.type = {
    this.id = Option(id)
    this
  }

  def setId(id: Int): this.type = {
    this.id = Option(id.toString())
    this
  }

  def setId(id: UUID): this.type = {
    this.id = Option(id.toString())
    this
  }
}

// TODO: make these typesafe (so I get a compile time error if a GetQuery hasn't setId())
// See here for directions: http://www.tikalk.com/java/blog/type-safe-builder-scala-using-type-constraints

// Get is for retrieving a singular representation
class GetQuery(resource: String) extends Query(resource) with Id

// Gets is for retrieving a list of multiple representations
class GetsQuery(resource: String) extends Query(resource)

class DeleteQuery(resource: String) extends Query(resource) with Id

class PutQuery(resource: String) extends Query(resource) with Id with Payload

class PostQuery(resource: String) extends Query(resource) with Payload

// TODO: add HEAD

trait QueryResult[A] { self =>
  def map[B](f: (A) => B): QueryResult[B] = new QueryResult[B] {
    def run(): B = f(self.run())
  }
  def flatMap[B](f: (A) => QueryResult[B]) = new QueryResult[B] {
    def run(): B = f(self.run()).run()
  }
  def run(): A
}