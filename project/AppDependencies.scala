import sbt._
import play.core.PlayVersion
import play.sbt.PlayImport._

private object AppDependencies {

  lazy val compile: Seq[ModuleID] = Seq(
    "com.typesafe.play" %% "play" % PlayVersion.current,
    ehcache,
    ws
  )

  lazy val test = Seq(
    "com.typesafe.play"       %% "play-test"                % PlayVersion.current % "test",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "5.1.0"             % "test",
    "org.mockito"             %% "mockito-scala-scalatest"  % "1.16.3"            % "test",
    "com.vladsch.flexmark"    %  "flexmark-all"             % "0.35.10"           % "test"
  )

  def apply(): Seq[ModuleID] = compile ++ test
}
