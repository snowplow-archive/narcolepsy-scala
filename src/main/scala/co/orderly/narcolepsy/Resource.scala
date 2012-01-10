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

/**
 * Resource defines a mapping from a URL slug (e.g. "products") to a Representation object.
 * Defining these for each resource in an API object allows Narcolepsy to know which type
 * of Representation or RepresentationWrapper to instantiate for a given resource access
 */
class Resource[
  R <: Representation,
  RW <: RepresentationWrapper[SR],
  SR <: Representation](
  slug: String,
  typeR:  Class[R],
  typeRW: Class[RW]
  ) {

  // -------------------------------------------------------------------------------------------------------------------
  // Client handling logic
  // -------------------------------------------------------------------------------------------------------------------

  // Holds the client used to access this resource
  protected var _client: Client = _

  def attachClient(client: Client) {
    this._client = client
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Query (API execution) logic
  // -------------------------------------------------------------------------------------------------------------------

  def get() = new GetQuery[R](_client, slug, typeR)

  def gets() = new GetsQuery[RW, SR](_client, slug, typeRW)

  def put() = new PutQuery[R](_client, slug, typeR)

  def post() = new PostQuery[R](_client, slug, typeR)

  def delete() = new DeleteQuery(_client, slug)

  // TODO: add HEAD
}
