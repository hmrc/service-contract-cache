import sbt._
import play.sbt.PlayImport._

private object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    ehcache,
    ws
  )

  val test = Seq(
    "uk.gov.hmrc"  %% "bootstrap-test-play-30"  % "8.2.0"  % Test
  )

  def apply(): Seq[ModuleID] = compile ++ test
}
