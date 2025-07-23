package zio.datastar.poc.api

import zio.*
import zio.datastar.poc.Datastar.CounterState
import zio.http.*
import zio.http.endpoint.*
import zio.http.template.Dom

object Endpoints {

  val `GET /`: Endpoint[Unit, Unit, ZNothing, Dom, AuthType.None] =
    Endpoint(Method.GET / Root)
      .out[Dom]

  val `POST /increment`: Endpoint[Unit, CounterState, ZNothing, ServerSentEvent[String], AuthType.None] =
    Endpoint(Method.POST / "increment")
      .in[CounterState]
      .out[ServerSentEvent[String]]

  val `POST /decrement`: Endpoint[Unit, CounterState, ZNothing, ServerSentEvent[String], AuthType.None] =
    Endpoint(Method.POST / "decrement")
      .in[CounterState]
      .out[ServerSentEvent[String]]

}
