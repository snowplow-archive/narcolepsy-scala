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

/**
 * Trait for the error format case objects
 */
trait ErrorFormat

/**
 * Meaning that this API returns all errors as its own custom form of ErrorRepresentation
 */
case object RepresentationErrors extends ErrorFormat

/**
 * Meaning that this API returns all errors as plaintext
 */
case object PlaintextErrors extends ErrorFormat

/**
 * Meaning that this API returns errors as a mix of plaintext and its own custom form of ErrorRepresentation
 */
case object MixedErrors extends ErrorFormat