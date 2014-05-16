
name := "peer-matching"

version := "1.0"


libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "mysql" % "mysql-connector-java" % "5.1.21",
  "org.mindrot" % "jbcrypt" % "0.3m"
  ) 
    
// Setup Play for Java
play.Project.playJavaSettings
