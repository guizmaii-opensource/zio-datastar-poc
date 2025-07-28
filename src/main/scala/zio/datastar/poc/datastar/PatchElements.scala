package zio.datastar.poc.datastar

import zio.http.ServerSentEvent
import zio.http.codec.HttpContentCodec
import zio.json.{EncoderOps, JsonEncoder}
import zio.schema.Schema

opaque type PatchElements = ServerSentEvent[String]
object PatchElements {
  private val eventType: Option[String] = Some("datastar-patch-elements")

  def apply(
    selector: String,
    data: String,
    mode: Option[ElementPatchMode] = None,
    useViewTransition: Option[Boolean] = None
  ): PatchElements = {
    val modeStr       = mode.fold(ifEmpty = "")(m => s" mode ${m.value}")
    val transitionStr = useViewTransition.fold(ifEmpty = "")(vt => s" useViewTransition ${vt.toString}")
    val selectorStr   = if (selector.nonEmpty) s" selector $selector" else ""

    ServerSentEvent(
      data = s"$selectorStr$modeStr$transitionStr\n$data",
      eventType = eventType
    )
  }

  def remove(selector: String, useViewTransition: Option[Boolean] = None): PatchElements = {
    val transitionStr = useViewTransition.fold(ifEmpty = "")(vt => s" useViewTransition ${vt.toString}")
    ServerSentEvent(
      data = s" selector $selector mode remove$transitionStr",
      eventType = eventType
    )
  }

  given HttpContentCodec[PatchElements] = ServerSentEvent.defaultContentCodec(using Schema.primitive[String])
}
