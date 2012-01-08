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
package adapters

// Narcolepsy
import utils.HttpMethod

trait HttpAdapter {

  /**
   * Handles an HTTP request to the web service
   * @param requestMethod HttpMethod to apply to this request
   * @param requestData The payload
   * @param requestUri Relative path to resource. Attach rootUri to get the absolute URI
   * @return The RestfulResponse (response code, response body and response header)
   */
  def execute(requestMethod: HttpMethod, requestData: Option[String], requestUri: String): RestfulResponse
}

/**
 * Flags an exception in the configuration of an HTTP library adapter - i.e. a subclass of the
 * above trait
 */
class HttpAdapterException(message: String = "") extends RuntimeException(message)