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
import Keys._

object BuildSettings {

  lazy val basicSettings = Seq[Setting[_]](
    organization  := "orderly",
    version       := "0.1",
    description   := "Narcolepsy is a Scala framework for building typesafe clients for RESTful web services",
    scalaVersion  := "2.9.1",
    scalacOptions := Seq("-deprecation", "-encoding", "utf8"),
    resolvers     ++= Dependencies.resolutionRepos
  )

  lazy val narcolepsySettings = basicSettings ++ seq(
  
    // Publishing
    // credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    crossPaths := false,
    publishMavenStyle := true,
    publishTo <<= version { version =>
      Some { // TODO: add snapshot support, Spray-style 
        Resolver.sftp("Orderly Maven repository", "xxx", 22, "/var/www/repo.orderly.co/prod/public/releases") as ("xxx", new java.io.File(/*util.Properties.userHome +*/ "/home/xxx/.ssh/id_rsa"))
      } // if (version.trim.endsWith("SNAPSHOT")) "snapshots/" else"releases/"
    }
  )
}
