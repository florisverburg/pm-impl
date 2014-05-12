// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.3")

// Add Checkstyle libraries.
libraryDependencies ++= Seq(
  "com.puppycrawl.tools" % "checkstyle" % "5.5"
)

// Add Jacoco plugin for code coverage
addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.1.4")

// Add Findbugs plugin library
addSbtPlugin("de.johoop" % "findbugs4sbt" % "1.3.0")
