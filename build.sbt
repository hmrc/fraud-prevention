/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import sbt._
import sbt.Keys._
import uk.gov.hmrc.DefaultBuildSettings.targetJvm
import uk.gov.hmrc.versioning.SbtGitVersioning

lazy val library = (project in file("."))
  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning)
  .settings(
    scalaVersion := "2.11.11",
    name := "fraud-prevention",
    targetJvm := "jvm-1.8",
    crossScalaVersions := Seq("2.11.11"),
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play"              % "2.5.12" % "provided",
      "commons-validator" %  "commons-validator" % "1.6",
      "org.scalatest"     %% "scalatest"         % "3.0.4"  % "test",
      "uk.gov.hmrc"       %% "hmrctest"          % "2.3.0"  % "test"
    ),
    resolvers := Seq(
      Resolver.bintrayRepo("hmrc", "releases")
    )
  )

// Coverage configuration
coverageMinimum := 100
coverageFailOnMinimum := true
coverageExcludedPackages := "<empty>;com.kenshoo.play.metrics.*;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;app.*;uk.gov.hmrc.BuildInfo"
