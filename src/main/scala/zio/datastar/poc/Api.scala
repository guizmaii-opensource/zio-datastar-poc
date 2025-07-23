package zio.datastar.poc

import zio.*
import zio.datastar.poc.Datastar.{CounterState, html}
import zio.http.*
import zio.json.*
import zio.stream.ZStream

object Api {

  private val contentTypeHtml: Headers = Headers(Header.ContentType(MediaType.text.html).untyped)

  val datastarRoutes: Routes[Any, Throwable] =
    Routes(
      // Serve HTML page
      Method.GET / Root -> handler(
        Response(
          Status.Ok,
          contentTypeHtml,
          Body.fromString(html),
        )
      ),

      // Increment endpoint
      Method.POST / "increment" -> handler { (req: Request) =>
        for {
          bodyStr      <- req.body.asString
          currentState <- ZIO
                            .fromEither(bodyStr.fromJson[CounterState])
                            .catchAll(_ => ZIO.succeed(CounterState(0))) // fallback
          newState = currentState.copy(count = currentState.count + 1)
          event    = Datastar.Events.patchSignals(newState.toJson)
          stream   = ZStream.succeed(event)
        } yield Response.fromServerSentEvents(stream)
      },

      // Decrement endpoint
      Method.POST / "decrement" -> handler { (req: Request) =>
        for {
          bodyStr      <- req.body.asString
          currentState <- ZIO
                            .fromEither(bodyStr.fromJson[CounterState])
                            .catchAll(_ => ZIO.succeed(CounterState(0))) // fallback
          newState = currentState.copy(count = currentState.count - 1)
          event    = Datastar.Events.patchSignals(newState.toJson)
          stream   = ZStream.succeed(event)
        } yield Response.fromServerSentEvents(stream)
      }
    )

}
