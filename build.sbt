scalaVersion := "2.10.2"

scalacOptions ++= Seq("-feature", "-deprecation")

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

organization := "no.mesan"

name := "fag.patterns.scala"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.3",
  "org.joda" % "joda-convert" % "1.3",
  "org.scalaj" % "scalaj-time_2.10.2" % "0.7",
  "org.apache.poi" % "poi-ooxml" % "3.9"
)

libraryDependencies ++= Seq( // test
  "junit" % "junit" % "4.11" % "test",
  "org.scalatest" %% "scalatest" % "2.0" % "test",
  "org.hamcrest" % "hamcrest-core" % "1.3" % "test",
  "org.mockito" % "mockito-core" % "1.9.5" % "test"
)

org.scalastyle.sbt.ScalastylePlugin.Settings