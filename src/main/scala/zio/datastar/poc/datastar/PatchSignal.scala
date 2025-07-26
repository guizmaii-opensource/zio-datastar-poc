package zio.datastar.poc.datastar

import zio.http.ServerSentEvent
import zio.http.codec.HttpContentCodec
import zio.json.{EncoderOps, JsonEncoder}
import zio.prelude.Subtype
import zio.schema.Schema

type PatchSignal[State] = PatchSignal.Type
object PatchSignal extends Subtype[ServerSentEvent[String]] {
  def apply[State: JsonEncoder](data: State): PatchSignal[State] =
    wrap(
      ServerSentEvent(
        data = s"signals ${data.toJson}",
        eventType = Some("datastar-patch-signals")
      )
    )

  /**
   * Needed otherwise the zio-http `Endpoint::outStream` method doesn't pick the correct `HttpContentCodec` implicit
   * and returns a HTTP response with `Content-Type: application/json` instead of `text/event-stream`.
   */
  given [State]: HttpContentCodec[PatchSignal[State]] = wrapAll(ServerSentEvent.defaultContentCodec(using Schema.primitive[String]))
}
