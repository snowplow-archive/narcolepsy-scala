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
import java.io.StringReader

// JAXB and XML
import javax.xml.bind.JAXBContext

// Narcolepsy
import adapters._
import utils._
import marshallers.json._
import marshallers.xml._

/**
 * Resource defines a mapping from a URL slug (e.g. "products") to a Representation object.
 * Defining these for each resource in an API object allows Narcolepsy to know which type
 * of Representation or RepresentationWrapper to instantiate for a given resource access
 */
class Resource[
  R <: Representation,
  RW <: RepresentationWrapper[_ <: Representation]](
  slug: String,
  typeR:  Class[R],
  typeRW: Class[RW]
  ) {

  // Private var to hold the client used to access this resource
  private var client: Client = _

  // -------------------------------------------------------------------------------------------------------------------
  // Client handling logic
  // -------------------------------------------------------------------------------------------------------------------

  def attachClient(client: Client) {
    this.client = client
  }

  // -------------------------------------------------------------------------------------------------------------------
  // GET verb methods
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * Retrieve (GET) a resource, self-assembly version without parameters
   * @return RESTful response from the API
   */
  def get(): GetResponse[R, RW] =
    get(None, None, true) // Expecting a wrapper representation (plural) back

  /**
   * Retrieve (GET) a resource, self-assembly version with parameters
   * @param params Map of parameters (e.g. 'filter' or 'sort') plus values
   * @return RESTful response from the API
   */
  def get(params: RestfulParams): GetResponse[R, RW] =
    get(None, Some(params), false) // Not expecting a wrapper representation back

  /**
   * Retrieve (GET) a resource, self-assembly version without parameters
   * @param id Resource ID to retrieve, in Integer form
   * @return RESTful response from the API
   */
  def get(id: Int): GetResponse[R, RW] =
    get(Some(id.toString()), None, false) // Not expecting a wrapper back

  // TODO: add UUID version

  /**
   * Retrieve (GET) a resource, self-assembly version without parameters
   * @param id Resource ID to retrieve, in String form
   * @return RESTful response from the API
   */
  def get(id: String): GetResponse[R, RW] =
    get(Some(id), None, false) // Not expecting a wrapper back

  /**
    * Retrieve (GET) a resource, self-assembly version with parameters
    * @param id Resource ID to retrieve, in Integer form
    * @param params Map of parameters (e.g. 'filter' or 'sort') plus values
    * @return RESTful response from the API
    */
  def get(id: Int, params: RestfulParams): GetResponse[R, RW] =
    get(Some(id.toString()), Some(params), false) // Not expecting a wrapper back

  // TODO: add UUID version

  /**
   * Retrieve (GET) a resource, self-assembly version with parameters
   * @param id Resource ID to retrieve, in String form
   * @param params Map of parameters (e.g. 'filter' or 'sort') plus values
   * @return RESTful response from the API
   */
  def get(id: String, params: RestfulParams): GetResponse[R, RW] =
    get(Some(id), Some(params), false) // Not expecting a wrapper back

  /**
   * Retrieve (GET) a resource, master version using Options (not invoked directly)
   * @param id Optional resource ID to retrieve, in String form
   * @param params Optional map of parameters (e.g. 'filter' or 'sort') plus values
   * @param wrapped Whether we expect back a wrapped set of multiple representations or a single representation
   * @return RESTful response from the API
   */
  protected def get(id: Option[String], params: Option[RestfulParams], wrapped: Boolean): GetResponse[R, RW] = {
    getUri(
      (slug +
      (if (id.isDefined) "/%s".format(id.get) else "") +
      (if (params.isDefined) "?%s".format(RestfulHelpers.canonicalize(params.get)) else "")),
      wrapped
    )
  }

  /**
   * Retrieve (GET) a resource, URL version
   * @param uri A URL which explicitly sets the resource type, ID(s) and parameters to retrieve
   * @param wrapped Whether we expect back a wrapped set of multiple representations or a single representation
   * @param jsonRoot TODO get rid of this - shouldn't be part of the call, should be an attribute on the Client definition
   * @return RESTful response from the API
   */
  def getUri(uri: String, wrapped: Boolean, jsonRoot: Option[Boolean] = None): GetResponse[R, RW] = {
    val (code, headers, body) = client.execute(GetMethod, None, uri)

    // TODO: add some proper validation / error handling, not this hacky stuff
    // Let's have a lambda/function for error detection
    val errored = (code != 200)

    // TODO: get rid of these debug messages
    Console.println("Response code: %s".format(code))
    Console.println(body.get)

    val unmarshaller = this.client.contentType match {
      case Some("application/json") => { // TODO: why is this Some() - content type really ought to just be a String by this point, not an Option(String)

        // If jsonRoot not explicitly set, set to the opposite of wrapped (because wrapped
        // representations typically have their "root" already included in the Jackson definition)
        val hasRoot = jsonRoot.getOrElse(!(wrapped))

        // Unmarshall the JSON
        UnmarshalJson(body.get, hasRoot)
      }
      case Some("text/xml") => UnmarshalXml(body.get)
      case _ => throw new ClientConfigurationException("Narcolepsy can only unmarshall JSON and XML currently") // TODO change exception type
    }

    // Whether we unmarshal a singular representation or a representation wrapper depends on wrapped:
    val r = if (wrapped) {
      Right(unmarshaller.toRepresentation[RW](typeRW))
    } else {
      Left(unmarshaller.toRepresentation[R](typeR))
    }
    // TODO: add some validation / error handling

    // Return the GetResponse Tuple3
    (code, r, errored)
  }

  // -------------------------------------------------------------------------------------------------------------------
  // DELETE verb methods
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * Delete (DELETE) a resource, self-assembly version supporting one ID
   * This version takes a resource type and an array of IDs to delete
   * @param resource The type of resource to delete (e.g. "orders")
   * @param id An ID of this resource type, to delete
   */
  def delete(id: String): DeleteResponse[R] =
    deleteUri(slug + "/" + id)

  /**
   * Delete (DELETE) a resource, self-assembly version supporting multiple IDs
   * This version takes a resource type and an array of IDs to delete
   * @param resource The type of resource to delete (e.g. "orders")
   * @param ids An array of IDs of this resource type, to delete
   */
  // TODO: this is PrestaShop-specific behaviour, need to sort this out
  def delete(ids: Array[String]): DeleteResponse[R] =
    deleteUri(slug + "/?id=[%s]".format(ids.mkString(",")))

  // TODO: add in Int and UUID versions

  /**
   * Delete (DELETE) a resource, URL version
   * @param url A URL which explicitly sets resource type and resource ID
   */
  def deleteUri(uri: String): DeleteResponse[R] = {

    // Perform the DELETE
    val (code, headers, body) = client.execute(DeleteMethod, None, uri)

    // TODO: add some validation & error handling
    val errored = false // TODO placeholder for now

    // Return the DeleteResponse Tuple3
    (code, None, errored) // TODO: update with something other than None
  }
}
