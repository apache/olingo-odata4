name := "odata-olingo"
scalaVersion in ThisBuild := "2.11.8"
javacOptions in ThisBuild ++= Seq("-encoding", "UTF-8", "-source", "1.6")

lazy val commonsApi = (project in file("lib/commons-api")).settings(
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11",
    libraryDependencies += "org.mockito" % "mockito-core" % "1.10.19",
    libraryDependencies += "commons-io" % "commons-io" % "2.5"
  )

lazy val commonsCore = (project in file("lib/commons-core")).dependsOn(commonsApi).settings(
    libraryDependencies += "commons-codec" % "commons-codec" % "1.9"
  )

lazy val serverApi = (project in file("lib/server-api")).dependsOn(commonsApi).settings(
    libraryDependencies += "javax.servlet" % "servlet-api" % "2.5" % Provided
  )

lazy val serverCore = (project in file("lib/server-core")).dependsOn(serverApi, commonsCore).settings(
    libraryDependencies += "javax.servlet" % "servlet-api" % "2.5" % Provided,
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.7.8",
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.7.8",
    libraryDependencies += "com.fasterxml" % "aalto-xml" % "0.9.10"
  )

