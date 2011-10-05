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

// Java
import java.io.StringReader

// JAXB and XML
import javax.xml.bind.JAXBContext

// Orderly
import adapters._
import utils._

/**
 * Resource defines a mapping from a URL slug (e.g. "products") to a Representation object.
 * Defining these for each resource in an API object allows Narcolepsy to know which type
 * of Representation or RepresentationWrapper to instantiate for a given resource access
 */
class Resource[
  R  <: Representation,
  RW <: RepresentationWrapper with Wraps[R]](
  slug: String,
  typeR:  Class[_ <: Representation],
  typeRW: Class[_ <: RepresentationWrapper]
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
  // Marshalling and unmarshalling logic
  //
  // Note: I (Alex) tried to move these unmarshalling methods to the Representation and
  // RepresentationWrapper (a la http://stackoverflow.com/questions/7616692/how-can-i-invoke-the-constructor-of-a-scala-abstract-type )
  // but couldn't figure out how to achieve it without massive over-complication. I think they're fine here.
  // -------------------------------------------------------------------------------------------------------------------

  def unmarshalXml(marshalledData: String): R = {

    val context = JAXBContext.newInstance(typeR)
    val representation = context.createUnmarshaller().unmarshal(
      new StringReader(marshalledData)
    ).asInstanceOf[R]

    representation // Return the representation
  }

  def unmarshalWrapperXml(marshalledData: String): List[R] = {

    val context = JAXBContext.newInstance(typeRW)
    val wrapper = context.createUnmarshaller().unmarshal(
      new StringReader(marshalledData)
    ).asInstanceOf[RW]

    wrapper.toList // Return the wrapper representation in List[] form
  }

  // -------------------------------------------------------------------------------------------------------------------
  // GET verb methods
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * Retrieve (GET) a resource, self-assembly version without parameters
   * @return RESTful response from the API
   */
  def get(): GetResponse[R] =
    get(None, None, true) // Expecting a wrapper representation (plural) back

  /**
   * Retrieve (GET) a resource, self-assembly version with parameters
   * @param params Map of parameters (one or more of 'filter', 'display', 'sort', 'limit')
   * @return RESTful response from the API
   */
  def get(params: RestfulParams): GetResponse[R] =
    get(None, Some(params), false) // Not expecting a wrapper representation back

  /**
   * Retrieve (GET) a resource, self-assembly version without parameters
   * @param id Resource ID to retrieve
   * @return RESTful response from the API
   */
  def get(id: String): GetResponse[R] =
    get(Some(id), None, false) // Not expecting a wrapper back

  /**
   * Retrieve (GET) a resource, self-assembly version with parameters
   * @param id Resource ID to retrieve
   * @param params Map of parameters (one or more of 'filter', 'display', 'sort', 'limit')
   * @return RESTful response from the API
   */
  def get(id: String, params: RestfulParams): GetResponse[R] =
    get(Some(id), Some(params), false) // Not expecting a wrapper back

  /**
   * Retrieve (GET) a resource, master version using Options (not invoked directly)
   * @param id Optional resource ID to retrieve
   * @param params Optional Map of parameters (one or more of 'filter', 'display', 'sort', 'limit')
   * @return RESTful response from the API
   */
  protected def get(id: Option[String], params: Option[RestfulParams], wrapped: Boolean): GetResponse[R] = {
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
   * @param wrapped Whether the returned representation will be a wrapper representation or a singular one
   * @return RESTful response from the API
   */
  def getUri(uri: String, wrapped: Boolean): GetResponse[R] = {
    val (code, headers, body) = client.execute(GetMethod, None, uri) // Execute the API call using GET. Injected dependency using Cake pattern

    // TODO: add some validation / error handling
    val errored = false // TODO placeholder for now

    // Whether we unmarshal a singular representation or a representation wrapper depends on wrapped:
    val r = if (wrapped) {
      Right(unmarshalWrapperXml(body.get))
    } else {
      Left(unmarshalXml(body.get))
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