ThisBuild / scalaVersion     := "2.13.2"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "io.vaan"

ThisBuild / scalacOptions ++= Seq(
  "-encoding", "utf8",
  "-Xfatal-warnings",
  "-Xverify",
  "-deprecation",
  "-unchecked",
  "-target:jvm-1.8",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps"
)

lazy val root = (project in file("."))
  .settings(
    name := "let me speak",
  )

libraryDependencies ++= Seq(
  "org.augustjune" %% "canoe" % "0.4.1"
)
