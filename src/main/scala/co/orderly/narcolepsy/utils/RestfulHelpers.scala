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
package utils

// Apache HttpClient
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.protocol.HTTP
import org.apache.http.message.BasicNameValuePair

// Scala
import scala.collection.JavaConversions._

/**
 * Useful methods for building RESTful clients
 */
object RestfulHelpers {

  /**
   * Returns a canonicalized, escaped string of &key=value pairs from a Map of parameters
   * @param params A map of parameters ('filter', 'display' etc)
   * @param encoding What character encoding to use for the canonicalized string
   * @return A canonicalized escaped string of the parameters
   */
  def canonicalize(params: RestfulParams, encoding: String): String = {

    val nameValues = params.map { param => new BasicNameValuePair(param._1, param._2) }
    URLEncodedUtils.format(nameValues.toSeq, encoding) // TODO: would be nice to remove scalaj as dependency
  }

  /**
   * Turns a set of RestfulHeaders (a Map[String, String]) into a single
   * string (with line breaks) ready for printing, logging or similar
   * @param headers The headers to pretty print
   * @return The headers in format "header-name: header-value\nheader-name: ..."
   */
  def stringify(headers: RestfulHeaders): String =
    headers.map(h => "%s: %s".format(h._1, h._2)).mkString("\n")

  /**
   * Naive implementation of a function for whether an HTTP status code
   * represents an error or not. Currently flags all client errors
   * (4xx) and server errors (5xx) as errors
   *
   */
  def isError(statusCode: Int): Boolean =
    (statusCode >= 400) // TODO: potentially make more sophisticated?
}