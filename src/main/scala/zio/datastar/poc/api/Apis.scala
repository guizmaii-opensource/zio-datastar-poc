package zio.datastar.poc.api

import zio.datastar.poc.Pages
import zio.datastar.poc.api.Endpoints.*
import zio.datastar.poc.datastar.PatchSignal
import zio.http.Routes
import zio.stream.ZStream

object Apis {

  def datastarRoutes: Routes[Any, Nothing] = Routes(home, increment, decrement)

  // Handlers for the endpoints
  private val home = `GET /`.implementAs(Pages.home)

  private val increment =
    `POST /increment`.implementPurely { currentState =>
      val newState = currentState.copy(count = currentState.count + 1)
      ZStream.succeed(PatchSignal(newState))
    }

  private val decrement =
    `POST /decrement`.implementPurely { currentState =>
      val newState = currentState.copy(count = currentState.count - 1)
      ZStream.succeed(PatchSignal(newState))
    }

}
