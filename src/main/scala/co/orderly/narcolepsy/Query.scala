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
package co.orderly.narcolepsy

class Query {
  def debugPrint(verbose: Boolean): this.type = { val _verbose = verbose; this }
  def throwIfError(): this.type = { this }
  def get(tpe: String, id: Int): QueryResult[RestfulResponse] =
    new QueryResult[RestfulResponse] {
       def run(): RestfulResponse = null // code to make rest call goes here
    }
}

trait QueryResult[A] { self =>
  def map[B](f: (A) => B): QueryResult[B] = new QueryResult[B] {
    def run(): B = f(self.run())
  }
  def flatMap[B](f: (A) => QueryResult[B]) = new QueryResult[B] {
    def run(): B = f(self.run()).run()
  }
  def run(): A
}