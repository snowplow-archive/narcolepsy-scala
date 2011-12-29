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
package co.orderly.narcolepsy.representations

// Orderly
import co.orderly.narcolepsy.Representation

/**
 * PlaintextError is a very simple Representation generated within Narcolepsy
 * to store a plaintext error returned by the web service (or set by Narcolepsy
 * itself) in place of the expected structured (JSON/XML) representation
 *
 * message is the plaintext error message itself
 *
 * internal flags that this error message occurred inside of Narcolepsy itself
 */
case class PlaintextError(message: String, internal: Boolean = false) extends Representation