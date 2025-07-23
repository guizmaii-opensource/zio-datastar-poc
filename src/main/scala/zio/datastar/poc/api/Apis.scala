package zio.datastar.poc.api

import zio.datastar.poc.Datastar
import zio.datastar.poc.Datastar.html
import zio.datastar.poc.api.Endpoints.*
import zio.http.Routes
import zio.json.*

object Apis {

  def datastarRoutes: Routes[Any, Nothing] = Routes(home, increment, decrement)

  // Handlers for the endpoints
  private val home = `GET /`.implementAs(html)

  private val increment =
    `POST /increment`.implementPurely { currentState =>
      val newState = currentState.copy(count = currentState.count + 1)
      Datastar.Events.patchSignals(newState.toJson)
    }

  private val decrement =
    `POST /decrement`.implementPurely { currentState =>
      val newState = currentState.copy(count = currentState.count - 1)
      Datastar.Events.patchSignals(newState.toJson)
    }

}
