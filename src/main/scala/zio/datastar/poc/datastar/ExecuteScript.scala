package zio.datastar.poc.datastar

import zio.http.ServerSentEvent
import zio.http.codec.HttpContentCodec
import zio.schema.Schema

opaque type ExecuteScript = ServerSentEvent[String]
object ExecuteScript {
  private val eventType: Option[String] = Some("datastar-execute-script")

  def apply(
    script: String,
    autoRemove: Option[Boolean] = None,
    attributes: Option[String] = None
  ): ExecuteScript = {
    val autoRemoveStr = autoRemove.fold(ifEmpty = "")(ar => s" autoRemove ${ar.toString}")
    val attributesStr = attributes.fold(ifEmpty = "")(attr => s" attributes $attr")

    ServerSentEvent(
      data = s"$autoRemoveStr$attributesStr\n$script",
      eventType = eventType
    )
  }

  given HttpContentCodec[ExecuteScript] = ServerSentEvent.defaultContentCodec(using Schema.primitive[String])
}
