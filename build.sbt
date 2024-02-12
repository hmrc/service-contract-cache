import scoverage.ScoverageKeys._

val appName = "service-contract-cache"

ThisBuild / scalaVersion := "2.13.12"
ThisBuild / majorVersion := 2

lazy val scoverageSettings = Seq(
  coverageExcludedPackages := "<empty>;.*BuildInfo*.",
  coverageMinimumStmtTotal := 100,
  coverageFailOnMinimum := true,
  coverageHighlighting := true
)

lazy val microservice = Project(appName, file("."))
  .enablePlugins(SbtAutoBuildPlugin)
  .settings(
    libraryDependencies ++= AppDependencies(),
  )
  .settings(scoverageSettings:_*)
