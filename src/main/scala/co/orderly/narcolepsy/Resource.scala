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

  // Holds the client used to access this resource
  protected var _client: Client = _

  // -------------------------------------------------------------------------------------------------------------------
  // Client handling logic
  // -------------------------------------------------------------------------------------------------------------------

  def attachClient(client: Client) {
    this._client = client
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Query (API execution) logic
  // -------------------------------------------------------------------------------------------------------------------

  def get(): GetQuery[R] = new GetQuery[R](_client, slug, typeR)

  def gets(): GetsQuery[RW] = new GetsQuery[RW](_client, slug, typeRW)

  def put(): PutQuery[R] = new PutQuery[R](_client, slug, typeR)

  def post(): PostQuery[R] = new PostQuery[R](_client, slug, typeR)

  def delete(): DeleteQuery = new DeleteQuery(_client, slug)

  // TODO: add HEAD

  /* LEGACY UNMARSHALLING CODE

  def getUri(uri: String, wrapped: Boolean, jsonRoot: Option[Boolean] = None): GetResponse[R, RW] = {
    val (code, headers, body) = client.execute(GetMethod, None, uri)

    // TODO: add some proper validation / error handling, not this hacky stuff
    // Let's have a lambda/function for error detection
    val errored = (code != 200)

    // TODO: get rid of these debug messages
    Console.println("Response code: %s".format(code))
    Console.println(body.get)

    // TODO: this unmarshalling code should be taken out of here. Should be associated to the Representations - i.e. a representation
    // TODO should have its own view on how it's unmarshalled

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
  } */
}
