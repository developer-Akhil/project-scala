import sbt.Credentials

name := "ETLBDSystem"

version := "0.1"

scalaVersion := "2.12.10"


val appVersion = "1.0.0-SNAPSHOT"
val org = "com.etl.gcp"
val orgName = "aruba Inc."
val scalaVer = "2.12.10"

version in ThisBuild := appVersion
organization in ThisBuild := org
scalaVersion in ThisBuild := scalaVer
organizationName in ThisBuild := orgName

val mergeStrategyVal = assemblyMergeStrategy in assembly := {
  case PathList(ps @ _*) if ps.last endsWith ".jar" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".thrift" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".xml" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".default" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".dat" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".fmpp" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".gif" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".dtd" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".xsd" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".ico" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".css" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".js" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".mf" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".jdo" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".txt" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ":BUILD" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".jar:BUILD" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "twitter-server_2.11-19.11.0.jar" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "finagle-core_2.11-19.11.0.jar" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "finagle-base-http_2.11-19.11.0.jar" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "finagle-netty4-http_2.11-19.11.0.jar" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "finagle-netty4_2.11-19.11.0.jar" => MergeStrategy.first
  case PathList("META-INF", xs @ _*)                => MergeStrategy.discard
  case "application.conf"                            => MergeStrategy.concat
  case "unwanted.txt"                                => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

lazy val config = (project in file("config"))

  .settings(addArtifact(artifact in (Compile, assembly), assembly))
  .settings(
    name := "config",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
    assemblyJarName in assembly := org+ "-"+ name +"-" +appVersion+".jar"
  )
  .settings(libraryDependencies ++= Seq("org.apache.spark" %% "spark-core" % "2.4.0",
    "com.typesafe" % "config" % "1.3.1",
    "org.apache.spark" %% "spark-sql" % "2.4.0",
    "com.google.code.gson" % "gson" % "2.8.4",
    "log4j" % "log4j" % "1.2.17",
    "org.apache.commons" % "commons-io" % "1.3.2"
  )).settings(mergeStrategyVal)

lazy val logger = (project in file("logger"))
  .settings(addArtifact(artifact in (Compile, assembly), assembly))
  .settings(
    name := "logger",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
    assemblyJarName in assembly := org+ "-"+ name +"-" +appVersion+".jar")
  .settings(libraryDependencies ++= Seq("org.scalaj" %% "scalaj-http" % "2.4.2"
  )).dependsOn(`config`)
  .settings(mergeStrategyVal)
  .aggregate(`config`)

lazy val utils = (project in file("utils"))
  .settings(addArtifact(artifact in (Compile, assembly), assembly))
  .settings(
    name := "utils",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
    assemblyJarName in assembly := org+ "-"+ name +"-" +appVersion+".jar")
  .settings(libraryDependencies ++= Seq("ch.qos.logback" % "logback-classic" % "1.1.7",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
    "com.github.pathikrit" %% "better-files" % "3.4.0",
    "com.databricks" % "spark-csv_2.11" % "1.5.0"
  ))
  .dependsOn(`config`,`logger`)
  .aggregate(`config`,`logger`)
  .settings(mergeStrategyVal)

lazy val sourcestoragesystem = (project in file("sourcestoragesystem"))
  .settings(addArtifact(artifact in (Compile, assembly), assembly))
  .settings(
    name := "sourcestoragesystem",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
    assemblyJarName in assembly := org+ "-"+ name +"-" +appVersion+".jar")
  .settings(libraryDependencies ++= Seq("org.apache.kafka" % "kafka-clients" % "2.1.0",
    "org.apache.spark" %% "spark-sql-kafka-0-10" %"2.4.0",
    "org.scalactic" %% "scalactic" % "3.0.1",
    "org.scalatest" %% "scalatest" % "3.0.1",
    "joda-time" % "joda-time" % "2.10.2",
    "org.joda" % "joda-convert" % "2.2.1",
    "io.getquill" %% "quill-core" % "3.4.10",
    "io.getquill" %% "quill-jdbc" % "3.4.10",
    "com.github.scopt" %% "scopt" % "3.7.1",
    "com.github.finagle" %% "finchx-core" % "0.29.0",
    "com.github.finagle" %% "finchx-circe" % "0.29.0" ,
    "com.twitter" %% "twitter-server" % "19.11.0",
    "io.circe" %% "circe-core" % "0.11.1",
    "io.circe" %% "circe-generic" % "0.11.1",
    "io.circe" %% "circe-parser" % "0.11.1",
    "io.netty" % "netty-all" % "4.1.43.Final"
  ))
  .dependsOn(`config`,`logger`,utils)
  .aggregate(`config`,`logger`, utils)
  .settings(mergeStrategyVal)

lazy val cryptographyalgo = (project in file("cryptographyalgo"))
  .settings(addArtifact(artifact in (Compile, assembly), assembly))
  .settings(
    name := "cryptographyalgo",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
    assemblyJarName in assembly := org+ "-"+ name +"-" +appVersion+".jar")
  .settings(libraryDependencies ++= Seq("org.bouncycastle" % "bcpg-jdk15" % "1.46",
    "org.bouncycastle" % "bcprov-jdk15" % "1.46"))
  .dependsOn(`config`,`logger`, utils,`sourcestoragesystem`)
  .aggregate(`config`,`logger`, utils,`sourcestoragesystem`)
  .settings(mergeStrategyVal)

lazy val deduplication = (project in file("deduplication"))
  .settings(addArtifact(artifact in (Compile, assembly), assembly))
  .settings(
    name := "deduplication",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
    assemblyJarName in assembly := org+ "-"+ name +"-" +appVersion+".jar")
  .settings(libraryDependencies ++= Seq())
  .dependsOn(`config`,`logger`, utils,sourcestoragesystem,`cryptographyalgo`)
  .aggregate(`config`,`logger`, utils,sourcestoragesystem,`cryptographyalgo`)
  .settings(mergeStrategyVal)

lazy val transformationprocess = (project in file("transformationprocess"))
  .settings(addArtifact(artifact in (Compile, assembly), assembly))
  .settings(
    name := "transformationprocess",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
    assemblyJarName in assembly := org+ "-"+ name +"-" +appVersion+".jar")
  .settings(libraryDependencies ++= Seq())
  .dependsOn(`config`,`logger`, utils,sourcestoragesystem,`cryptographyalgo`)
  .aggregate(`config`,`logger`, utils,sourcestoragesystem,`cryptographyalgo`)
  .settings(mergeStrategyVal)

//lazy val dumptodatalake = (project in file("dumptodatalake"))
//  .settings(addArtifact(artifact in (Compile, assembly), assembly))
//  .settings(
//    name := "dumptodatalake",
//    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
//    assemblyJarName in assembly := org+ "-"+ name +"-" +appVersion+".jar")
//  .settings(libraryDependencies ++= Seq())
//  .dependsOn(`config`,`logger`, utils,sourcestoragesystem,`deduplication`,`transformationprocess`,`cryptographyalgo`)
//  .aggregate(`config`,`logger`, utils,sourcestoragesystem,`deduplication`,`transformationprocess`,`cryptographyalgo`)
//  .settings(mergeStrategyVal)

lazy val ETLBDSystem = (project in file("."))
  .settings(addArtifact(artifact in (Compile, assembly), assembly))
  .settings(name := "ETLBDSystem",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
    assemblyJarName in assembly := org+ "-"+ name +"-" +appVersion+".jar",
    publishTo := {
      val nexus = "http://nexus.rxcorp.com:8081/nexus/"
      if (version.value.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "repository/snapshots/")
      else
        Some("releases"  at nexus + "repository/releases/")
    },

    credentials += {
      Credentials("Sonatype Nexus Repository Manager", "nexus.rxcorp.com", "deployment", "deployment")
    },libraryDependencies ++= Seq())
  .dependsOn(`config`, `logger`,utils,sourcestoragesystem,`deduplication`,`transformationprocess`,`cryptographyalgo`)
  .aggregate(`config`, `logger`,utils,sourcestoragesystem,`deduplication`,`transformationprocess`,`cryptographyalgo`)
  .settings(mergeStrategyVal)