package zio.datastar.poc

import zio.datastar.poc.api.Endpoints
import zio.datastar.poc.extensions.{datastarAction, datastarState}
import zio.datastar.poc.macros.datastarFieldName
import zio.http.*
import zio.http.template.Dom
import zio.json.*
import zio.schema.{Schema, derived}

object Datastar {

  final case class CounterState(count: Int) derives Schema, JsonCodec
  object CounterState {
    val initial: CounterState = CounterState(count = 0)

    // Field name extraction using macro
    val countField: String = datastarFieldName[CounterState](_.count)
  }

  // Datastar SSE helper
  object Events {
    def patchSignals(json: String): ServerSentEvent[String] =
      ServerSentEvent(
        data = s"signals $json",
        eventType = Some("datastar-patch-signals")
      )
  }

  // HTML page with Datastar
  def html: Dom =
    Dom.raw(
      s"""
         |<html>
         |<head>
         |   <title>Simple Datastar Counter</title>
         |   <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@main/bundles/datastar.js"></script>
         |</head>
         |<body>
         |   <div data-signals='${CounterState.initial.datastarState}'>
         |       <h1>Counter: <span data-text="${CounterState.countField}"></span></h1>
         |       <button data-on-click="${Endpoints.`POST /increment`.datastarAction}">+</button>
         |       <button data-on-click="${Endpoints.`POST /decrement`.datastarAction}">-</button>
         |   </div>
         |</body>
         |</html>""".stripMargin.trim
    )

}
