/*
 * Copyright 2015 HM Revenue & Customs
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

private object AppDependencies {

  import play.core.PlayVersion
  import play.sbt.PlayImport._

  val compile: Seq[ModuleID] = Seq(
    "com.typesafe.play" %% "play" % PlayVersion.current,
    cache,
    ws
  )

  object Test {
    def apply(): Seq[ModuleID] = Seq(
      "com.typesafe.play" %% "play-test" % PlayVersion.current % "test",
      "org.scalatest" %% "scalatest" % "3.0.8" % "test",
      "org.pegdown" % "pegdown" % "1.6.0" % "test",
      "org.scalacheck" %% "scalacheck" % "1.14.0" % "test",
      "org.mockito" % "mockito-core" % "3.2.4" % "test"
    )
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}
