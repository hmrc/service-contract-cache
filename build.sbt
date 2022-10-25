import scoverage.ScoverageKeys

val appName = "service-contract-cache"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(SbtAutoBuildPlugin)
  .settings(
    scalaVersion := "2.13.8",
    libraryDependencies ++= AppDependencies(),
    majorVersion := 1
  )
  .settings(scoverageSettings:_*)

lazy val scoverageSettings = Seq(
  // Semicolon-separated list of regexs matching classes to exclude
  ScoverageKeys.coverageExcludedPackages := "<empty>;.*BuildInfo*.",
  ScoverageKeys.coverageMinimum := 100,
  ScoverageKeys.coverageFailOnMinimum := true,
  ScoverageKeys.coverageHighlighting := true
)
