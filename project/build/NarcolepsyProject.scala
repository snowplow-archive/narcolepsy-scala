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

import sbt._
import Process._

/**
 * NarcolepsyProject configuration capturing all dependencies
 */
class NarcolepsyProject(info: ProjectInfo) extends DefaultProject(info) {

  // -------------------------------------------------------------------------------------------------------------------
  // Common dependencies
  // -------------------------------------------------------------------------------------------------------------------

  // For Maven-based API versioning
  val mavenArtifact = "org.apache.maven" % "maven-artifact" % "3.0.3"

  // scalaj for asJava etc
  val scalajCollection = "org.scalaj" %% "scalaj-collection" % "1.1"

  // -------------------------------------------------------------------------------------------------------------------
  // Dependencies for using Apache HttpClient adapter
  // -------------------------------------------------------------------------------------------------------------------

  // Apache HttpCore
  val httpcore = "org.apache.httpcomponents" % "httpcore" % "4.1.1"

  // Apache HttpClient module
  val httpcomponentsClient = "org.apache.httpcomponents" % "httpclient" % "4.1.1"

  // -------------------------------------------------------------------------------------------------------------------
  // Extra resources to include in the .jar
  // -------------------------------------------------------------------------------------------------------------------

  // Include Apache license and README file
  def extraResources = "LICENSE-2.0.txt" +++ "README.markdown"
  override def mainResources = super.mainResources +++ extraResources
}
