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

// Spray
import cc.spray._
import http._ // To get HttpRequest etc

// Orderly
import utils.RestfulHelpers

/**
 * Resource defines a mapping from a URL slug (e.g. "products") to a Representation object.
 * Defining these for each resource in an API object allows Narcolepsy to know which type
 * of Representation or RepresentationWrapper to instantiate for a given resource access
 */
class Resource[
  R  <: Representation,
  RW <: RepresentationWrapper](
  slug: String,
  typeR:  Class[_ <: Representation],
  typeRW: Class[_ <: RepresentationWrapper]
  ) {

  this: Client =>

  // -------------------------------------------------------------------------------------------------------------------
  // Marshalling and unmarshalling logic
  // -------------------------------------------------------------------------------------------------------------------

  // TODO: add in marshall()

  def unmarshall(marshalledData: String): R = {

    val context = JAXBContext.newInstance(typeR)
    val representation = context.createUnmarshaller().unmarshal(
      new StringReader(marshalledData)
    ).asInstanceOf[R]

    representation // Return the representation
  }

  def unmarshallWrapper(marshalledData: String): List[R] = {

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
  def get(): (Int, Either[R, List[R]], Boolean) = {
    get(None, None)
  }

  /**
   * Retrieve (GET) a resource, self-assembly version with parameters
   * @param params Map of parameters (one or more of 'filter', 'display', 'sort', 'limit')
   * @return RESTful response from the API
   */
  def get(params: RestfulParams): (Int, Either[R, List[R]], Boolean) = {
    get(None, Some(params))
  }

  /**
   * Retrieve (GET) a resource, self-assembly version without parameters
   * @param id Resource ID to retrieve
   * @return RESTful response from the API
   */
  def get(id: String): (Int, Either[R, List[R]], Boolean) = {
    get(Some(id), None)
  }

  /**
   * Retrieve (GET) a resource, self-assembly version with parameters
   * @param id Resource ID to retrieve
   * @param params Map of parameters (one or more of 'filter', 'display', 'sort', 'limit')
   * @return RESTful response from the API
   */
  def get(id: String, params: RestfulParams): (Int, Either[R, List[R]], Boolean) = {
    get(Some(id), Some(params))
  }

  /**
   * Retrieve (GET) a resource, helper version using Options
   * @param id Optional resource ID to retrieve
   * @param params Optional Map of parameters (one or more of 'filter', 'display', 'sort', 'limit')
   * @return RESTful response from the API
   */
  protected def get(id: Option[String], params: Option[RestfulParams]): (Int, Either[R, List[R]], Boolean) = {
    getUri(
      slug +
      (if (id.isDefined) "/%s".format(id.get) else "") +
      (if (params.isDefined) "?%s".format(RestfulHelpers.canonicalize(params.get)) else "")
    )
  }

  /**
   * Retrieve (GET) a resource, URL version
   * @param uri A URL which explicitly sets the resource type, ID(s) and parameters to retrieve
   * @return RESTful response from the API
   */
  def getUri(uri: String): (Int, Either[R, List[R]], Boolean) = {
    val (code, responseString) = execute(slug, HttpMethods.GET, uri) // Execute the API call using GET. Injected dependency using Cake pattern

    val representationList = unmarshallWrapper(responseString)

    (code, Right(representationList), false) // TODO need to add in error handling etc
  }
}