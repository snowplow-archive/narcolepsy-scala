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

// Java
import java.net.URLEncoder
import java.io.InputStream

// Apache HttpClient
import org.apache.http.StatusLine
import org.apache.http.message._
import org.apache.http.auth._
import org.apache.http.params._
import org.apache.http.client._
import org.apache.http.client.methods._
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.impl.client._
import org.apache.http.protocol.HTTP

// Orderly
import orderly.narcolepsy.utils._

class ApacheHttpClientAdapter extends HttpAdapter {

  // Private immutable copy of an Apache HttpClient, we use this to access the API
  private val httpClient = new DefaultHttpClient

  /**
   * Handles an HTTP request to the web service
   * @param requestMethod HttpMethod to apply to this request
   * @param requestData The payload
   * TODO: shouldn't be a String, should be a list of something.
   * @param requestUri Relative path to resource. Attach rootUri to get the absolute URI
   * @return The RestfulResponse (response code, response body and response header)
   */
  def execute(requestMethod: HttpMethod, requestData: Option[String], requestUri: String): RestfulResponse = {

    // Construct the right type of HttpRequest object for our given HttpMethod,
    // and validate that our requestData payload is set appropriately
    val request = (requestMethod, requestData) match {
      case (GetMethod,    _)      => HttpGet(requestUri)      // _ because a GET action may have a payload
      case (DeleteMethod, _)      => HttpDelete(requestUri)   // _ because a DELETE action may have a payload
      case (PutMethod, Some(d))   => HttpPut(requestUri)
      case (PostMethod,Some(d))   => HttpPost(requestUri)
      case (PutMethod, None)      => throw new HttpAdapterException("Request data missing for HTTP PUT action")
      case (PutMethod, None)      => throw new HttpAdapterException("Request data missing for HTTP POST action")
      case _                      => throw new HttpAdapterException("Http action not supported")
    } // TODO: move to spray-style HttpMethod.GET structure so we can specify the unsupported verb in the error message

    // Configure the authentication
    httpClient.getCredentialsProvider().setCredentials(
      new AuthScope(request.getURI.getHost, request.getURI.getPort),
      new UsernamePasswordCredentials(apiKey, "")
    )

    // Attach the XML to the request if we have some - how we pass it in depends on whether it's a POST or PUT
    /* TODO - also figure out how encodeXML works with JSON
    request match {
      case r:HttpPut => r.setEntity(encodeXML("", xml.get))
      case r:HttpPost => r.setEntity(encodeXML("xml", xml.get))
      case _ =>
    } */

    // Execute the request and retrieve the response code and headers
    val response = httpClient.execute(request)
    val code = check(response.getStatusLine())
    val headers = response.getAllHeaders()

    // Now get the response body if we have one
    val responseEntity = Option(response.getEntity())
    val data = responseEntity match {
      case None => None
      case _ => Option(Source.fromInputStream(responseEntity.get.getContent()).mkString)
    }

    // Finally let's return the RestfulResponse
    (code, headers, data)
  }
}