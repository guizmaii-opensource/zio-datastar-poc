package zio.datastar.poc.api

import zio.*
import zio.datastar.poc.Pages
import zio.datastar.poc.api.Endpoints.*
import zio.datastar.poc.datastar.*
import zio.http.Routes
import zio.stream.ZStream
import zio.json.ast.Json

object Apis {

  def datastarRoutes: Routes[DataStore, Nothing] =
    Routes(
      home,
      increment,
      decrement,
      updateContent,
      appendItem,
      executeAlert,
      updateUserData
    )

  // Handlers for the endpoints
  private val home = `GET /`.implementAs(Pages.home)

  private val increment =
    `POST /increment`.implementPurely { currentState =>
      val newState = currentState.increment
      ZStream.succeed(PatchSignal(newState))
    }

  private val decrement =
    `POST /decrement`.implementPurely { currentState =>
      val newState = currentState.decrement
      ZStream.succeed(PatchSignal(newState))
    }

  // Example: Replace content of an element
  private val updateContent =
    `POST /update-content`.implementPurely { _ =>
      val newContent =
        """
          |<div id="content" class="updated">
          |  <h2>Content Updated!</h2>
          |  <p>This content was replaced using PatchElements</p>
          |</div>""".stripMargin.trim

      ZStream.succeed(
        PatchElements(
          selector = "#content",
          data = newContent,
          mode = Some(ElementPatchMode.Outer),
          useViewTransition = Some(true)
        )
      )
    }

  // Example: Append items to a list
  private val appendItem =
    `POST /append-item`.implementPurely { itemText =>
      val newItem = s"""<li class="list-item">$itemText</li>"""

      ZStream.succeed(
        PatchElements(
          selector = "#item-list",
          data = newItem,
          mode = Some(ElementPatchMode.Append),
          useViewTransition = Some(false)
        )
      )
    }

  // Example: Execute JavaScript alert
  private val executeAlert =
    `POST /execute-alert`.implementPurely { message =>
      val script = s"""alert('Server says: $message');"""

      ZStream.succeed(
        ExecuteScript(
          script = script,
          autoRemove = Some(true)
        )
      )
    }

  // Example: Using DataStore - simplified without SignalReader for now
  private val updateUserData =
    `POST /update-user-data`.implement { _ =>
      ZIO.serviceWithZIO[DataStore] { store =>
        // Example: Add some server-side data to the store
        val addServerData = store.put("lastUpdated", Json.Str(java.time.Instant.now.toString))

        // Get all signals and send back as patch
        for {
          _         <- addServerData
          signals   <- store.toMap
          // Convert Json values to strings for this example
          strSignals = signals.view.mapValues {
                         case Json.Str(s) => s
                         case other       => other.toString
                       }.toMap
        } yield ZStream.succeed(PatchSignal(strSignals))
      }
    }

}
