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

// Narcolepsy
import adapters._
import utils._

// TODO: remove these
import marshallers.jackson.UnmarshalJson
import marshallers.jaxb.UnmarshalXml

/**
 * Query is a fluent interface for constructing a call (GET, POST, DELETE, PUT or
 * similar) to a RESTful web service. It is typed so that the representations
 * can be (un)marshalled in a typesafe way.
 */
abstract class Query[
  R <: Representation](
  method: HttpMethod,
  client: Client,
  resource: String,
  typeR: Class[R]) {

  // TODO 1: would be good to make the Query builder typesafe. So e.g. developer gets a compile time error if a GetQuery hasn't setId()
  // TODO: see here for directions: http://www.tikalk.com/java/blog/type-safe-builder-scala-using-type-constraints

  // TODO 2: it would also be quite nice to make the Query builder immutable, rather than use vars

  // -------------------------------------------------------------------------------------------------------------------
  // Flags for the stateful builder
  // -------------------------------------------------------------------------------------------------------------------

  protected var _payload: Option[String] = None

  protected var _id: Option[String] = None

  protected var _params: Option[RestfulParams] = None

  protected var _slug: String = resource

  protected var _print: Boolean = false

  protected var _exception: Boolean = false

  // -------------------------------------------------------------------------------------------------------------------
  // Fluent methods which can be used in any Query builder
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * Add this method to set on debug-style printing of the query execution
   * @return The updated Query builder
   */
  def print(): this.type = {
    this._print = true
    this
  }

  /**
   * Add this method to override the resource 'slug' used for this query
   * @param slug
   * @return The updated Query builder
   */
  def slug(slug: String): this.type = {
    this._slug = slug
    this
  }

  /**
   * Add this method to throw an exception if we received an HTTP error code back from the web service
   * @return The updated Query builder
   */
  def exception(): this.type = {
    this._exception = true
    this
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Execution methods for the Query
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * run() executes the query using all of the parameters set (or not set) through
   * the builder.
   * @return A RestfulResponse tuple of return code, HTTP headers and body
   */
  def run(): RestfulResponse = {

    val uri = (_slug +
      (if (_id.isDefined) "/%s".format(_id.get) else "") +
      (if (_params.isDefined) "?%s".format(RestfulHelpers.canonicalize(_params.get)) else "")
      )

    if (_print) {
      Console.println("Executing Narcolepsy query against URI: /%s".format(uri))
    }

    val (code, headers, body) = client.execute(method, _payload, uri)

    if (_print) {
      Console.println("Response status code: %s".format(code))
      Console.println("Response headers:\n%s".format(RestfulHelpers.stringify(headers)))
      Console.println("Response body:\n%s".format(body.getOrElse("<< EMPTY >>")))
    }

    // TODO: check if we have an error and throw if we do

    (code, headers, body)
  }

  /**
   * unmarshal() executes the query using run() and then unmarshals the result into
   * the appropriate Representation object
   */
  def unmarshal(): UnmarshalledResponse[_ <: ErrorRepresentation, R] = {

    val (code, _, body) = run()

    // TODO: I want to decouple this using implicit objects and conversions, Spray-style
    if (RestfulHelpers.isError(code)) {
      Left(RestfulError(code, body, null)) // TODO add error unmarshalling in here
    } else {
      Right(body map( b =>
        client.contentType match {

          // TODO: Remove Some() around the content types. No need for it
          case Some("application/json") => UnmarshalXml(body.get).toRepresentation[R](typeR)
          case Some("text/xml") => UnmarshalXml(body.get).toRepresentation[R](typeR)
          case _ => throw new ClientConfigurationException("Narcolepsy can only unmarshall JSON and XML currently") // TODO change exception type
        }))
    }
  }
}

// -------------------------------------------------------------------------------------------------------------------
// Specific fluent functionalities only found on some Query subclasses
// -------------------------------------------------------------------------------------------------------------------

/**
 * Payload allows a Query to have a 'payload' attached. A payload is data submitted to the web service with the
 * request. Typically used by PUT and POST requests.
 */
trait Payload {

  // Grab _payload from Query
  self: {
    var _payload: Option[String]
  } =>

  def payload(payload: String): this.type = {
    this._payload = Option(payload)
    this
  }
}

/**
 * Id allows a Query to have a resource ID attached. This is used by any Query which wants to operate on a specific
 * (already existing) resource, rather than a new resource. Typically used by all DELETE and POST requests, and
 * some GET requests.
 */
trait Id {

  // Grab _id from Query
  self: {
    var _id: Option[String]
  } =>

  // TODO: add support for multiple IDs. For example PrestaShop supports DELETEing /?id=45,65. Need to make it play nice with other parameters

  def id(id: String): this.type = {
    this._id = Option(id)
    this
  }

  def id(id: Int): this.type = {
    this._id = Option(id.toString())
    this
  }

  def id(id: Long): this.type = {
    this._id = Option(id.toString())
    this
  }

  def id(id: UUID): this.type = {
    this._id = Option(id.toString())
    this
  }
}

// -------------------------------------------------------------------------------------------------------------------
// Define the concrete Query subclasses using the traits above
// -------------------------------------------------------------------------------------------------------------------

/**
 * GetQuery is for retrieving a singular representation. Applies the GetMethod and uses the Id trait
 */
class GetQuery[R <: Representation](client: Client, resource: String, typeR: Class[R])
  extends Query[R](GetMethod, client, resource, typeR)
  with Id

/**
 * GetsQuery is for retrieving a list of multiple representations. From an HTTP/RESTful perspective, a
 * GetQuery and a GetsQuery are identical: they both execute a GET. From a Narcolepsy typesafety
 * perspective they are quite different:
 *  - A GetQuery takes an ID and returns a singular representation which can be unmarshalled to a Representation subclass
 *  - A GetsQuery takes no ID and returns a collection-style representation which can be unmarshalled to a RepresentationWrapper subclass
 */
class GetsQuery[RW <: RepresentationWrapper[_ <: Representation]](client: Client, resource: String, typeRW: Class[RW])
  extends Query[RW](GetMethod, client, resource, typeRW)

/**
 * DeleteQuery is for deleting a resource. Applies the DeleteMethod and uses the Id trait
 */
class DeleteQuery(client: Client, resource: String)
  extends Query(DeleteMethod, client, resource, null) // TODO: null not very clean here
  with Id

// TODO: add doccomment
class PutQuery[R <: Representation](client: Client, resource: String, typeR: Class[R])
  extends Query[R](PutMethod, client, resource, typeR)
  with Id
  with Payload

// TODO: add doccomment
class PostQuery[R <: Representation](client: Client, resource: String, typeR: Class[R])
  extends Query[R](PostMethod, client, resource, typeR)
  with Payload

// TODO: add HeadQuery

// -------------------------------------------------------------------------------------------------------------------
// Exceptions
// -------------------------------------------------------------------------------------------------------------------

/**
 * Flags that running the Query returned a non-success code
 */
class ResponseCodeException(message: String = "") extends RuntimeException(message)