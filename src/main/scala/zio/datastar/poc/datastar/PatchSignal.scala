package zio.datastar.poc.datastar

import zio.datastar.poc.extensions.Extended
import zio.http.ServerSentEvent
import zio.json.{EncoderOps, JsonEncoder}
import zio.prelude.Subtype

type PatchSignal[State] = PatchSignal.Type
object PatchSignal extends Subtype[ServerSentEvent[String]] with Extended[ServerSentEvent[String]] {
  def apply[State: JsonEncoder](data: State): PatchSignal[State] =
    wrap(
      ServerSentEvent(
        data = s"signals ${data.toJson}",
        eventType = Some("datastar-patch-signals")
      )
    )
}
