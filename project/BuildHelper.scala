import Libraries.*
import _root_.org.typelevel.sbt.tpolecat.TpolecatPlugin.autoImport.*
import org.typelevel.scalacoptions.ScalacOptions
import sbt.*
import sbt.Keys.*

object BuildHelper {

  private val javaTarget = "21"

  def env(v: String): Option[String] = sys.env.get(v)
  def unsafeEnv(v: String): String   = sys.env(v)

  lazy val stdSettings =
    noDoc ++ releaseModeScalacOptions ++ Seq(
      javacOptions ++= Seq("-source", javaTarget, "-target", javaTarget),
      scalacOptions ++= Seq("-no-indent"),                          // See https://x.com/ghostdogpr/status/1706589471469425074
      scalacOptions ++= Seq("-language:noAutoTupling"),             // See https://github.com/scala/scala3/discussions/19255
      scalacOptions ++= Seq(s"-release:$javaTarget"),
      scalacOptions ++= Seq(
        "-experimental",
        "-preview", // See https://www.scala-lang.org/news/3.7.1/#preview-features
      ),
      scalacOptions ++= Seq(
        "-Xmax-inlines",
        "64" // Increase inline limit from default 32 to 64
      ),
      scalacOptions --= (if (insideCI.value) Nil else Seq("-Xfatal-warnings")),
      // format: off
      tpolecatScalacOptions ++= Set(
        ScalacOptions.privateBackendParallelism(), // See https://github.com/typelevel/sbt-tpolecat/blob/main/plugin/src/main/scala/io/github/davidgregory084/ScalacOptions.scala#L409-L424
      ),
      // format: on
      libraryDependencies ++= Seq(zio, prelude, zioLogging) ++ tests.map(_ % Test),
      excludeDependencies ++= Seq(ExclusionRule("log4j", "log4j")), // security vulnerabilities
      // Fix for PG failing to start in tests when run locally.
      // The issue is that because they are run in parallel, they all try to bind to the directory at the same time.
      // This is a workaround to make sure that each sbt module uses a different directory when PG starts.
      // See:
      //  https://github.com/zonkyio/embedded-postgres/blob/6b85d7106eb31e04b0acec460e2c66e534e62df3/src/main/java/io/zonky/test/db/postgres/embedded/EmbeddedPostgres.java#L473
      Test / javaOptions += {
        val pgWorkdir = new java.io.File(System.getProperty("java.io.tmpdir"), s"embedded-pg-${name.value}")
        s"-Dot.epg.working-dir=${pgWorkdir.getAbsolutePath}"
      },
      Test / fork := true,
    )

  private val releaseModeScalacOptions = Seq(tpolecatReleaseModeOptions ++= SbtTpolecatExtensions.optimiserConfig)

  lazy val noDoc = Seq(
    (Compile / doc / sources)                := Seq.empty,
    (Compile / packageDoc / publishArtifact) := false,
  )

}
