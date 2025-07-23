import BuildHelper.*
import Libraries.*

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / envFileName       := ".env"
ThisBuild / organization      := "zio.datastar.poc"
ThisBuild / version           := "0.0.1-SNAPSHOT"
ThisBuild / scalaVersion      := "3.7.1"
ThisBuild / scalafmtCheck     := true
ThisBuild / scalafmtSbtCheck  := true
ThisBuild / scalafmtOnCompile := !insideCI.value
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision // use Scalafix compatible version
ThisBuild / usePipelining     := true                        // Scala 3.5+
ThisBuild / resolvers += Resolver.sonatypeCentralSnapshots

// ### Aliases ###

// compilation commands
addCommandAlias("tc", "Test/compile")
addCommandAlias("ctc", "clean; tc")
addCommandAlias("rctc", "reload; ctc")
// Start, stop, restart the application
// Use `~rst` to restart the application automatically on file changes
addCommandAlias("start", "~reStart")
addCommandAlias("stop", "reStop")
addCommandAlias("restart", "reStart")
addCommandAlias("rst", "reStart")

// ### Modules ###

lazy val root =
  Project(id = "zio-datastar-poc", base = file("."))
    .enablePlugins(BuildInfoPlugin)
    .settings(stdSettings *)
    .settings(
      // BuildInfo settings
      buildInfoPackage := "zio.datastar.poc",
      buildInfoObject  := "PocBuildInfo",
      buildInfoKeys    := Seq[BuildInfoKey](version),
    )
    .settings(Logging.excludeLogbackDevConfFromJar *)
    .settings(Revolver.enableDebugging(5005))
    .settings(reLogTag := "datastar-poc")
    .settings(Compile / mainClass := Some("zio.datastar.poc.Main"))
    .settings(libraryDependencies ++= zioHttp ++ zioConfig ++ loggingRuntime)


