package zio.datastar.poc.api

import zio.*
import zio.datastar.poc.datastar.PatchSignal
import zio.datastar.poc.states.CounterState
import zio.http.*
import zio.http.endpoint.*
import zio.http.template.Dom
import zio.stream.UStream

object Endpoints {

  val `GET /`: Endpoint[Unit, Unit, ZNothing, Dom, AuthType.None] =
    Endpoint(Method.GET / Root)
      .out[Dom]

  val `POST /increment`: Endpoint[Unit, CounterState, ZNothing, UStream[PatchSignal[CounterState]], AuthType.None] =
    Endpoint(Method.POST / "increment")
      .in[CounterState]
      .outStream[PatchSignal[CounterState]]

  val `POST /decrement`: Endpoint[Unit, CounterState, ZNothing, UStream[PatchSignal[CounterState]], AuthType.None] =
    Endpoint(Method.POST / "decrement")
      .in[CounterState]
      .outStream[PatchSignal[CounterState]]

}
