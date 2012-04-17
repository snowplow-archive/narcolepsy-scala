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

// Java
import java.util.UUID

// Narcolepsy
import adapters._
import utils._

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

  protected var payload: Option[String] = None

  protected val _client: Client = client // Because can't explicit self type on a class constructor arg

  protected var id: Option[String] = None

  protected var params: Option[RestfulParams] = None

  protected var slug: String = resource

  protected var console: Boolean = false

  protected var exception: Boolean = false

  // -------------------------------------------------------------------------------------------------------------------
  // Fluent methods which can be used in any Query builder
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * Switches on debug-style printing of the query execution
   * @return The updated Query builder
   */
  def consolePrint(): this.type = {
    this.console = true
    this
  }

  /**
   * Overrides the resource 'slug' used for this query
   * @param slug
   * @return The updated Query builder
   */
  def overrideSlug(slug: String): this.type = {
    this.slug = slug
    this
  }

  /**
   * Throws an exception if we received an HTTP error code back from the web service
   * @return The updated Query builder
   */
  def throwException(): this.type = {
    this.exception = true
    this
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Execution methods for the Query
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * Executes the query using all of the parameters set (or not set) through
   * the builder.
   * @return A RestfulResponse tuple of return code, HTTP headers and body
   */
  def run(): RestfulResponse = {

    val uri = (slug +
      (if (id.isDefined) "/%s".format(id.get) else "") +
      (if (params.isDefined) "?%s".format(RestfulHelpers.canonicalize(params.get, client.configuration.encoding)) else "")
      )

    if (console) {
      Console.println("Executing Narcolepsy query against URI: /%s".format(uri))
    }

    val (code, headers, body) = client.execute(method, payload, uri)

    if (console) {
      Console.println("Response status code: %s".format(code))
      Console.println("Response headers:\n%s".format(RestfulHelpers.stringify(headers)))
      Console.println("Response body:\n%s".format(body.getOrElse("<< EMPTY >>")))
    }

    // TODO: check if we have an error and throw if we do

    (code, headers, body)
  }

  /**
   * Executes the query using run() and then unmarshals the result into
   * the appropriate Representation object
   */
  def unmarshal(): UnmarshalledResponse[_ <: ErrorRepresentation, R] = {

    val (code, _, body) = run()

    // TODO: I want to decouple this using implicit objects and conversions, Spray-style
    if (RestfulHelpers.isError(code)) {
      Left(RestfulError(code, body, null)) // TODO: add unmarshalling of errors in here
    } else {
      Right(body map( r => _client.unmarshaller.toRepresentation(client.configuration.contentType, r, typeR)))
        // TODO: pass in client.configuration.contentType

          // case "application/json" => null // UnmarshalJson(b, true).toRepresentation[R](typeR) // TODO: remove rootKey bool
          // case "text/xml" => null // UnmarshalXml(b).toRepresentation[R](typeR)
        //  case _ => throw new ClientConfigurationException("Narcolepsy can only unmarshal JSON and XML currently, not %s".format(client.configuration.contentType))
        // }))
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
trait Payload[R <: Representation] {

  // Grab _payload from Query
  self: {
    var payload: Option[String]
    val _client: Client
  } =>

  /**
   * Add a payload which is type-bound to the same representation as
   * the containing Resource.
   */
  def addSelfPayload(representation: R): this.type = {
    this.payload = Option(_client.marshaller.fromRepresentation(
      _client.configuration.contentType,
      representation)
    )
    this
  }

  /**
   * Add any marshalled payload.
   */
  def addPayload(payload: String): this.type = {
    this.payload = Option(payload)
    this
  }

  /**
   * Add any representation payload.
   */
  def addPayload[A <: Representation](representation: A): this.type = {
    this.payload = Option(_client.marshaller.fromRepresentation(
      _client.configuration.contentType,
      representation)
    )
    this
  }
}

/**
 * Id allows a Query to have a resource ID attached. This is used by any Query which wants to operate on a specific
 * (already existing) resource, rather than a new resource. Typically used by all DELETE and POST requests, and
 * some GET requests.
 */
trait Id {

  // Grab id from Query
  self: {
    var id: Option[String]
  } =>

  // TODO: add support for multiple IDs. For example PrestaShop supports DELETEing /?id=45,65. Need to make it play nice with other parameters

  def setId(id: String): this.type = {
    this.id = Option(id)
    this
  }

  def setId(id: Int): this.type = {
    this.id = Option(id.toString())
    this
  }

  def setId(id: Long): this.type = {
    this.id = Option(id.toString())
    this
  }

  def setId(id: UUID): this.type = {
    this.id = Option(id.toString())
    this
  }
}

trait Listable[RW <: RepresentationWrapper[_]] {

  self: {
    def unmarshal(): UnmarshalledResponse[_ <: ErrorRepresentation, _ <: RepresentationWrapper[_]]
    val exception: Boolean
  } =>

  /**
   * toList runs a command and unmarshals it, then either decomposes the unmarshalled
   * object into a List[SR] or returns Nil if that's not possible
   */
  def toList(): List[RW#rtype] =

    // Pattern match on the unmarshal output
    unmarshal() match {
      case Left(error) => Nil // Empty list if we received an error
      case Right(None) => Nil // Empty list if our unmarshalled object is empty
      case Right(Some(data: RepresentationWrapper[RW#rtype])) => data.toList // Turn our unmarshalled object into a list
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
class GetsQuery[RW <: RepresentationWrapper[_]](client: Client, resource: String, typeRW: Class[RW])
  extends Query[RW](GetMethod, client, resource, typeRW)
  with Listable[RW]

/**
 * DeleteQuery is for deleting a resource. Applies the DeleteMethod and uses the Id trait
 */
class DeleteQuery(client: Client, resource: String)
  extends Query(DeleteMethod, client, resource, null) // TODO: null not very clean here
  with Id

/**
 * PutQuery is for performing a PUT on a resource. This is typically used for updating
 * an existing resource
 */
class PutQuery[R <: Representation](client: Client, resource: String, typeR: Class[R])
  extends Query[R](PutMethod, client, resource, typeR)
  with Id
  with Payload[R]

/**
 * PostQuery is for performing a POST on a resource. This is typically used for creating
 * an all-new resource
 */
class PostQuery[R <: Representation](client: Client, resource: String, typeR: Class[R])
  extends Query[R](PostMethod, client, resource, typeR)
  with Payload[R]

// TODO: add HeadQuery

// -------------------------------------------------------------------------------------------------------------------
// Exceptions
// -------------------------------------------------------------------------------------------------------------------

/**
 * Flags that running the Query returned a non-success code
 */
class ResponseCodeException(message: String = "") extends RuntimeException(message)