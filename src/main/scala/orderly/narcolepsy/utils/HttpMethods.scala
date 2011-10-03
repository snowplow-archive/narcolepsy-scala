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

/**
 * Trait for the HTTP method case objects
 */
trait HttpMethod

/**
 * DELETE verb, used for deletes
 */
case object DeleteMethod extends HttpMethod

/**
 * GET verb, used for reads
 */
case object GetMethod extends HttpMethod

/**
 * POST verb, typically used for xxx
 */
case object PostMethod extends HttpMethod

/**
 * PUT verb, typically used for xxx
 */
case object PutMethod extends HttpMethod

/**
 * HEAD verb, typically used for reading metadata only
 */
case object HeadMethod extends HttpMethod