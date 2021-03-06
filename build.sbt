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

import sbt.Keys._
import sbt._
import uk.gov.hmrc.DefaultBuildSettings.targetJvm
import uk.gov.hmrc.SbtArtifactory.autoImport.makePublicallyAvailableOnBintray
import uk.gov.hmrc.{SbtArtifactory, SbtAutoBuildPlugin}
import uk.gov.hmrc.versioning.SbtGitVersioning
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val library = (project in file("."))
  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, SbtArtifactory)
  .settings(
    scalaVersion := "2.11.11",
    name := "fraud-prevention",
    majorVersion := 0,
    makePublicallyAvailableOnBintray := true,
    targetJvm := "jvm-1.8",
    crossScalaVersions := Seq("2.11.11"),
    scalacOptions += "-Ypartial-unification",
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play"      % "2.5.19" % "provided",
      "org.scalatest"     %% "scalatest" % "3.0.4"  % "test",
      "org.typelevel"     %% "cats-core" % "1.1.0",
      "uk.gov.hmrc"       %% "hmrctest"  % "3.3.0"  % "test"
    ),
    resolvers := Seq(
      Resolver.bintrayRepo("hmrc", "releases")
    )
  )

// Coverage configuration
coverageMinimum := 90
coverageFailOnMinimum := true
coverageExcludedPackages := "<empty>;com.kenshoo.play.metrics.*;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;app.*;uk.gov.hmrc.BuildInfo"
