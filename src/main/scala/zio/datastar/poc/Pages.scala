package zio.datastar.poc

import zio.datastar.poc.api.Endpoints
import zio.datastar.poc.extensions.{datastarAction, datastarState}
import zio.datastar.poc.states.CounterState
import zio.http.template.Dom

object Pages {

  def home: Dom =
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
