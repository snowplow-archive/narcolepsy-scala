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
package orderly.narcolepsy.adapters

// Orderly
import orderly.narcolepsy.utils.HttpMethod

trait HttpAdapter {

  /**
   * Initialises the Apache HttpClient with configuration variables
   * @param username Username to authenticate this request
   * @param password Password to authenticate this request
   * @param userAgent Identifies this client to the API
   */
  def initialize(username: String, password: String, userAgent: String)

  /**
   * Handles an HTTP request to the web service
   * @param requestMethod HttpMethod to apply to this request
   * @param requestData The payload
   * TODO: shouldn't be a String, should be a list of something.
   * @param requestUri Relative path to resource. Attach rootUri to get the absolute URI
   * @return The RestfulResponse (response code, response body and response header)
   */
  def execute(requestMethod: HttpMethod, requestData: Option[String], requestUri: String): RestfulResponse
}

/**
 * Flags an exception in the configuration of an HTTP library adapter - i.e. a subclass of the
 * above trait
 */
class HttpAdapterException(message: String = "") extends RuntimeException(message) {
}