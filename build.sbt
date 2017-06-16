organization := "it.sciencespir"
name := "smart-budget"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.11"
logLevel := Level.Debug

resolvers += Resolver.bintrayRepo("hmrc", "releases")
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

val http4sVersion = "0.15.11a"
val slickVersion = "3.2.0"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-dsl"          % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-argonaut"     % http4sVersion,
  "com.github.alexarchambault" %% "argonaut-shapeless_6.2" % "1.2.0-M4",
  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5",
  "com.github.t3hnar" %% "scala-bcrypt" % "3.0",
  "uk.gov.hmrc" %% "emailaddress" % "2.0.0",

  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "com.h2database" % "h2" % "1.4.194",
  "com.github.tminglei" %% "slick-pg" % "0.15.0-RC",

  "com.github.nscala-time" %% "nscala-time" % "2.16.0",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0",


  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7",

  "org.scalaz" %% "scalaz-core" % "7.2.0",

  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.typelevel" %% "scalaz-scalatest" % "1.1.0" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
)

enablePlugins(DockerPlugin)

dockerAutoPackageJavaApplication()
