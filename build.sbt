organization := "it.sciencespir"
name := "smart-budget"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.8"
logLevel := Level.Debug

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % "0.15.2a",
  "org.http4s" %% "http4s-dsl"          % "0.15.2a",
  "org.http4s" %% "http4s-argonaut"     % "0.15.2a",

  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
  "org.postgresql" % "postgresql" % "9.4.1212",
  "com.github.nscala-time" %% "nscala-time" % "2.16.0",
  "com.github.tminglei" %% "slick-pg" % "0.14.2",
  "com.github.tminglei" %% "slick-pg_joda-time" % "0.14.2",

  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7",

  "org.scalaz" %% "scalaz-core" % "7.2.0"
)