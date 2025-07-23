package zio.datastar.poc

import zio.http.*
import zio.http.template.Dom
import zio.json.*

object Datastar {

  final case class CounterState(count: Int) derives JsonCodec

  // Datastar SSE helper
  object Events {
    def patchSignals(json: String): ServerSentEvent[String] =
      ServerSentEvent(
        data = s"signals $json",
        eventType = Some("datastar-patch-signals")
      )
  }

  // HTML page with Datastar
  val html: Dom =
    Dom.raw(
      """
        |<html>
        |<head>
        |   <title>Simple Datastar Counter</title>
        |   <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@main/bundles/datastar.js"></script>
        |</head>
        |<body>
        |   <div data-signals='{"count": 0}'>
        |       <h1>Counter: <span data-text="$count"></span></h1>
        |       <button data-on-click="@post('/increment')">+</button>
        |       <button data-on-click="@post('/decrement')">-</button>
        |   </div>
        |</body>
        |</html>""".stripMargin.trim
    )

}
