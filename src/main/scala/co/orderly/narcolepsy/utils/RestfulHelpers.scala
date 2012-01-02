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
package co.orderly.narcolepsy.utils

// Apache HttpClient
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.protocol.HTTP
import org.apache.http.message.BasicNameValuePair

// scalaj for asJava
import scalaj.collection.Imports._

// Orderly
import co.orderly.narcolepsy._

// TODO: let's get rid of this one

object RestfulHelpers {

  // -------------------------------------------------------------------------------------------------------------------
  // Constants used internally by the RestfulHelpers
  // -------------------------------------------------------------------------------------------------------------------

  // TODO: move this into the client definition

  // For URL encoding
  protected val CHARSET = HTTP.UTF_8;

  // -------------------------------------------------------------------------------------------------------------------
  // Useful methods for building RESTful clients
  // -------------------------------------------------------------------------------------------------------------------

}