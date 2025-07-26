package zio.datastar.poc.datastar

import zio.http.ServerSentEvent
import zio.http.codec.HttpContentCodec
import zio.json.{EncoderOps, JsonEncoder}
import zio.schema.Schema

opaque type PatchSignal[State] = ServerSentEvent[String]
object PatchSignal {
  private val eventType: Option[String] = Some("datastar-patch-signals")

  def apply[State: JsonEncoder](data: State): PatchSignal[State] =
    ServerSentEvent(
      data = s"signals ${data.toJson}",
      eventType = eventType
    )

  /**
   * Needed otherwise the zio-http `Endpoint::outStream` method doesn't pick the correct `HttpContentCodec` implicit
   * and returns a HTTP response with `Content-Type: application/json` instead of `text/event-stream`.
   */
  given [State]: HttpContentCodec[PatchSignal[State]] = ServerSentEvent.defaultContentCodec(using Schema.primitive[String])
}
