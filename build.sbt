scalaVersion := "2.11.6"

scalacOptions ++= Seq("-feature", "-deprecation")

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

organization := "no.mesan"

name := "fag.patterns.scala"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.7",
  "org.joda" % "joda-convert" % "1.7",
  "org.scalaj" % "scalaj-time_2.11" % "0.5",
  "org.apache.poi" % "poi-ooxml" % "3.12"
)

libraryDependencies ++= Seq( // test
  "junit" % "junit" % "4.12" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.hamcrest" % "hamcrest-core" % "1.3" % "test",
  "org.mockito" % "mockito-core" % "1.10.19" % "test"
)

// org.scalastyle.sbt.ScalastylePlugin.Settings