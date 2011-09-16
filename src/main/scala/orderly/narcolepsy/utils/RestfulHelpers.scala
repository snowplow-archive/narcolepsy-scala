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
import orderly.narcolepsy.utils.{RestfulTypes => RT}
// import orderly.narcolepsy.representations.RestfulRepresentation

object RestfulHelpers {

  // -------------------------------------------------------------------------------------------------------------------
  // Constants used by the RestfulHelpers
  // -------------------------------------------------------------------------------------------------------------------

  // For URL encoding
  // TODO are vals protected or public by default?
  protected val CHARSET = HTTP.UTF_8;

  // -------------------------------------------------------------------------------------------------------------------
  // Useful methods for building RESTful clients
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * Returns a canonicalized, escaped string of &key=value pairs from a Map of parameters
   * @param params A map of parameters ('filter', 'display' etc)
   * @return A canonicalized escaped string of the parameters
   */
  def canonicalize(params: RT.RestfulParams): String = {

    val nameValues = params.map { param => new BasicNameValuePair(param._1, param._2) }
    URLEncodedUtils.format(nameValues.toSeq.asJava, CHARSET)
  }
}