resolvers += "HMRC-open-artefacts-maven" at "https://open.artefacts.tax.service.gov.uk/maven2"

resolvers += Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns)

addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "3.0.0")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.24")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")