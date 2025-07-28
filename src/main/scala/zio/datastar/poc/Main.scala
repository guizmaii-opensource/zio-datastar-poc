package zio.datastar.poc

import zio.*
import zio.ZIOAspect.annotated
import zio.datastar.poc.api.Apis.datastarRoutes
import zio.datastar.poc.datastar.DataStore
import zio.http.*
import zio.http.Middleware.*
import zio.http.netty.NettyConfig
import zio.http.netty.NettyConfig.LeakDetectionLevel
import zio.logging.backend.SLF4J

import java.lang.Runtime as JRuntime

object Main extends ZIOAppDefault {

  private val appName: String = "datastar-poc"

  /**
   * See
   *  - `zio-logging` documentation: https://zio.github.io/zio-logging/docs/overview/overview_slf4j
   *  - https://x.com/guizmaii/status/1703395450571694425?s=20
   *  - https://blog.pierre-ricadat.com/tuning-zio-for-high-performance
   *
   * In ZIO 2.1.2+:
   * - the auto-blocking behavior is disabled by default, which is what we want
   * - the eager shift back is disabled by default, but we want it so we enable it here
   * - the fiber roots are enabled by default, but we don't want it for performance reasons
   *
   * (Read Pierre Ricadat's blog post for me info)
   */
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    (Runtime.removeDefaultLoggers >>> SLF4J.slf4j) ++
      Runtime.disableFlags(RuntimeFlag.FiberRoots) ++
      Runtime.enableFlags(RuntimeFlag.EagerShiftBack)

  private val internalServerError: Response =
    Response(
      status = Status.InternalServerError,
      body = Body.fromCharSequence("Internal Server Error"),
    )

  /**
   * Adapted from [[Server.default]]
   */
  private val server: ZLayer[Port, Throwable, Server] =
    ZLayer.makeSome[Port, Server](
      Server.customized,
      ZLayer.fromFunction((port: Port) => zio.http.Server.Config.default.port(port).idleTimeout(60.seconds)),
      ZLayer.succeed(NettyConfig.default.leakDetection(LeakDetectionLevel.ADVANCED)),
      ZLayer.fromZIO(ZIO.serviceWithZIO[Port](p => ZIO.logInfo(s"$appName started on port: $p"))),
    )

  private val `/health`: Routes[Any, Nothing] =
    Routes(
      Method.GET / "version"            -> Handler.text(PocBuildInfo.version),
      Method.GET / "ping"               -> Handler.text("pong"),
      Method.GET / "name"               -> Handler.text(appName),
      Method.GET / "health" / "up"      -> Handler.ok, // Must be used to know if the service is up or still booting
      Method.GET / "health" / "running" -> Handler.ok, // Must be used to know if the service is still running
    )

  // TODO Jules: Not prod ready
  private val corsConfig: CorsConfig = CorsConfig(allowedOrigin = _ => Some(Header.AccessControlAllowOrigin.All))

  private val api = {
    val datastarApis =
      datastarRoutes
        .mapErrorZIO(e =>
          ZIO
            .logErrorCause("SHOULD NEVER HAPPEN: Unexpected API error happened", Cause.fail(e))
            .as(internalServerError)
        ) @@ cors(corsConfig)
        @@ debug
        @@ timeout(1.minute)
        @@ dropTrailingSlash

    datastarApis ++ `/health`
  }

  private val bootSequence: Task[Unit] =
    for {
      < <- ZIO.logInfo(s"Booting $appName v${PocBuildInfo.version}")
      _ <- ZIO.logInfo(s"Runtime.version:             ${JRuntime.version()}".trim)
      _ <- ZIO.logInfo(s"Runtime.availableProcessors: ${JRuntime.getRuntime.availableProcessors()}".trim)
      _ <- ZIO.logInfo(s"Runtime.maxMemory:           ${JRuntime.getRuntime.maxMemory()}".trim)
      _ <- ZIO.logInfo(s"Runtime.totalMemory:         ${JRuntime.getRuntime.totalMemory()}".trim)
      _ <- ZIO.fail(new RuntimeException("Mono-threaded app")).whenDiscard(JRuntime.getRuntime.availableProcessors() == 1)
    } yield ()

  override def run: ZIO[Environment & ZIOAppArgs & Scope, Any, Any] =
    (
      (
        for {
          _ <- ZIO.withClockScoped(Clock.ClockJava(java.time.Clock.systemUTC())) // Must be the first thing executed
          _ <- (bootSequence *> Server.serve(api))
                 .tapDefect(cause => ZIO.logErrorCause(s"App crashed: ${cause.trace.toString()}", cause))
        } yield ()
      ) @@ annotated("version", PocBuildInfo.version)
    ).provideSomeAuto(
      server,
      ZLayer.succeed(Port(8080)),
      DataStore.live,
    )
}
