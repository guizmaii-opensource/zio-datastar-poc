package zio.datastar.poc.datastar

import zio.http.ServerSentEvent
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

  given [State]: Schema[PatchSignal[State]] = wrapAll(ServerSentEvent.schema[String])
}
