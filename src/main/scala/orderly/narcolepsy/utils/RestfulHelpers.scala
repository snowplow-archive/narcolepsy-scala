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
package orderly.narcolepsy.utils

// Apache HttpClient
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.protocol.HTTP
import org.apache.http.message.BasicNameValuePair

// scalaj for asJava
import scalaj.collection.Imports._

// Orderly
import orderly.narcolepsy._
// import orderly.narcolepsy.representations.RestfulRepresentation

object RestfulHelpers {

  // -------------------------------------------------------------------------------------------------------------------
  // Constants used by the RestfulHelpers
  // -------------------------------------------------------------------------------------------------------------------

  // For URL encoding
  val CHARSET = HTTP.UTF_8;

  // -------------------------------------------------------------------------------------------------------------------
  // Type definitions used for expressing RESTful attributes
  // -------------------------------------------------------------------------------------------------------------------

  // TODO: remove this. Temporary to test the API client without fannying around with JAXB
  type RestfulRepresentation = String

  // The return type for an API response.
  // Holds return code, either one representation or multiple, and a flag
  // indicating whether the representation is an error or not.
  // TODO: look at how squeryl deals with returning one row or multiple
  type RestfulResponse = (Int, Either[RestfulRepresentation, List[RestfulRepresentation]], Boolean)

  // Simple synonym for the API parameters
  type RestfulParams = Map[String, String]

  // -------------------------------------------------------------------------------------------------------------------
  // Useful methods for building RESTful clients
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * Returns a canonicalized, escaped string of &key=value pairs from a Map of parameters
   * @param params A map of parameters ('filter', 'display' etc)
   * @return A canonicalized escaped string of the parameters
   */
  def canonicalize(params: RestfulParams): String = {

    val nameValues = params.map { param => new BasicNameValuePair(param._1, param._2) }
    URLEncodedUtils.format(nameValues.toSeq.asJava, CHARSET)
  }
}