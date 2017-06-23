organization := "it.sciencespir"
name := "smart-budget"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.11"
logLevel := Level.Debug

resolvers += Resolver.bintrayRepo("hmrc", "releases")
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases"
resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases"
resolvers += "Tatami Releases" at "https://raw.github.com/cchantep/tatami/master/releases/"
  

val http4sVersion = "0.15.11a"
val slickVersion = "3.2.0"
val googleVersion = "1.22.0"

libraryDependencies ++= Seq(
  // Http4s
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-dsl"          % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-argonaut"     % http4sVersion,
  "com.github.alexarchambault" %% "argonaut-shapeless_6.2" % "1.2.0-M4",
  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5",
  "com.github.t3hnar" %% "scala-bcrypt" % "3.0",
  "uk.gov.hmrc" %% "emailaddress" % "2.0.0",

  // Slick
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "com.h2database" % "h2" % "1.4.194",
  "com.github.tminglei" %% "slick-pg" % "0.15.0-RC",

  // Time & Date
  "com.github.nscala-time" %% "nscala-time" % "2.16.0",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0",

  // Logging
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7",

  // Scalaz
  "org.scalaz" %% "scalaz-core" % "7.2.0",

  // Google Sheets
  "com.google.api-client" % "google-api-client" % googleVersion,
  "com.google.oauth-client" % "google-oauth-client" % googleVersion,
  "com.google.apis" % "google-api-services-sheets" % "v4-rev478-1.22.0",


  // Testing
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.typelevel" %% "scalaz-scalatest" % "1.1.0" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",

  "com.typesafe" % "config" % "1.3.1"
)

enablePlugins(DockerPlugin)

dockerAutoPackageJavaApplication()
