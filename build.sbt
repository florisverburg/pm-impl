
name := "peer-matching"

version := "1.0"


libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "mysql" % "mysql-connector-java" % "5.1.21",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.mockito" % "mockito-core" % "1.8.5"
)

// Setup Play for Java
play.Project.playJavaSettings

templatesImport ++= Seq(
  "helpers._",
  "play.i18n.Messages"
)
