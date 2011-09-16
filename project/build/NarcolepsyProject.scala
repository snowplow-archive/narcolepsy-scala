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

class NarcolepsyProject(info: ProjectInfo) extends DefaultWebProject(info) with AkkaProject {

  // -------------------------------------------------------------------------------------------------------------------
  // All repositories *must* go here! See ModuleConfigurations below.
  // -------------------------------------------------------------------------------------------------------------------
  object Repositories {
    // e.g. val AkkaRepo = MavenRepository("Akka Repository", "http://akka.io/repository")

    // Added by Alex to we can add in javax.servlet later
    val GlassFishRepo = MavenRepository("GlassFishRepo Repo", "http://download.java.net/maven/glassfish/")
  }

  // -------------------------------------------------------------------------------------------------------------------
  // ModuleConfigurations
  // Every dependency that cannot be resolved from the built-in repositories (Maven Central and Scala Tools Releases)
  // must be resolved from a ModuleConfiguration. This will result in a significant acceleration of the update action.
  // Therefore, if repositories are defined, this must happen as def, not as val.
  // -------------------------------------------------------------------------------------------------------------------
  import Repositories._

  // -------------------------------------------------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------------------------------------------------

  // these are the ones that are absolutely required for a project using spray-client
  val sprayHttp           = "cc.spray" %% "spray-http" % "0.7.0" % "compile" withSources()
  val sprayClient         = "cc.spray" %% "spray-client" % "0.7.0" % "compile" withSources()
  override val akkaActor  = akkaModule("actor") withSources() // it's good to always have the sources around

  // slf4j is not required but a good option for logging
  val akkaSlf4j = akkaModule("slf4j") withSources()
  val logback   = "ch.qos.logback" % "logback-classic" % "0.9.29" % "runtime" // a good logging backend for slf4j

  // For Maven-based API versioning
  val mavenArtifact = "org.apache.maven" % "maven-artifact" % "3.0.3"

  // For useful HttpClient functionality
  val httpcore = "org.apache.httpcomponents" % "httpcore" % "4.1.1"
  val httpcomponentsClient = "org.apache.httpcomponents" % "httpclient" % "4.1.1"

  // scalaj for asJava etc
  val scalajCollection = "org.scalaj" %% "scalaj-collection" % "1.1"

  // For Jackson
  val jacksonCoreLgpl = "org.codehaus.jackson" % "jackson-core-lgpl" % "1.8.4"
  val jacksonMapperLgpl = "org.codehaus.jackson" % "jackson-mapper-lgpl" % "1.8.4"
  val jacksonXc = "org.codehaus.jackson" % "jackson-xc" % "1.8.4"

  // for testing
  val JETTY_VERSION = "8.0.0.M3" // e.g. "7.2.0.v20101020" for testing the Jetty7ConnectorServlet
  val specs2 = "org.specs2" %% "specs2" % "1.5" % "test" withSources()
  val jettyServer = "org.eclipse.jetty" % "jetty-server" % JETTY_VERSION % "test"
  val jettyWebApp = "org.eclipse.jetty" % "jetty-webapp" % JETTY_VERSION % "test"

  override def testFrameworks = super.testFrameworks ++ Seq(new TestFramework("org.specs2.runner.SpecsFramework"))

  // Extra resources to include in the .jar
  def extraResources = "LICENSE-2.0.txt" +++ "README.markdown"
  override def mainResources = super.mainResources +++ extraResources
}
