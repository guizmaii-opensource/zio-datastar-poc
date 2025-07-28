package zio.datastar.poc.datastar

import zio.*
import zio.http.ServerSentEvent
import zio.http.codec.TextBinaryCodec.fromSchema
import zio.json.*
import zio.json.ast.Json
import zio.test.*
import zio.schema.Schema

object PatchSignalSpec extends ZIOSpecDefault {

  given zio.schema.codec.BinaryCodec[String] = fromSchema(Schema.primitive[String])

  final case class TestState(foo: Int, bar: Int) derives JsonCodec

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("PatchSignalSpec")(
      test("should encode basic signals correctly") {
        val state       = TestState(foo = 1, bar = 2)
        val patchSignal = PatchSignal(state)
        val event       = patchSignal.asInstanceOf[ServerSentEvent[String]]
        val result      = event.encode

        assertTrue(
          result ==
            """|data: signals {"foo":1,"bar":2}
               |event: datastar-patch-signals
               |
               |""".stripMargin
        )
      },
      test("should encode signals with onlyIfMissing when true") {
        val state       = TestState(foo = 1, bar = 2)
        val patchSignal = PatchSignal(state, onlyIfMissing = Some(true))
        val event       = patchSignal.asInstanceOf[ServerSentEvent[String]]
        val result      = event.encode

        assertTrue(
          result ==
            """|data: onlyIfMissing true
               |data: signals {"foo":1,"bar":2}
               |event: datastar-patch-signals
               |
               |""".stripMargin
        )
      },
      test("should encode signals with onlyIfMissing when false") {
        val state       = TestState(foo = 1, bar = 2)
        val patchSignal = PatchSignal(state, onlyIfMissing = Some(false))
        val event       = patchSignal.asInstanceOf[ServerSentEvent[String]]
        val result      = event.encode

        assertTrue(
          result ==
            """|data: onlyIfMissing false
               |data: signals {"foo":1,"bar":2}
               |event: datastar-patch-signals
               |
               |""".stripMargin
        )
      },
      test("should not include onlyIfMissing when None") {
        val state       = TestState(foo = 1, bar = 2)
        val patchSignal = PatchSignal(state, onlyIfMissing = None)
        val event       = patchSignal.asInstanceOf[ServerSentEvent[String]]
        val result      = event.encode

        assertTrue(
          result ==
            """|data: signals {"foo":1,"bar":2}
               |event: datastar-patch-signals
               |
               |""".stripMargin
        )
      },
      test("should encode complex nested state") {
        final case class NestedState(
          user: Map[String, String],
          count: Int,
          active: Boolean
        ) derives JsonCodec

        val state       = NestedState(
          user = Map("name" -> "John", "email" -> "john@example.com"),
          count = 42,
          active = true
        )
        val patchSignal = PatchSignal(state)
        val event       = patchSignal.asInstanceOf[ServerSentEvent[String]]
        val result      = event.encode

        assertTrue(
          result ==
            """|data: signals {"user":{"name":"John","email":"john@example.com"},"count":42,"active":true}
               |event: datastar-patch-signals
               |
               |""".stripMargin
        )
      },
      test("should handle null values for signal removal") {
        // Using Json directly to create explicit null values
        val nullSignals = Map("foo" -> Json.Null, "bar" -> Json.Null)
        val patchSignal = PatchSignal(nullSignals)
        val event       = patchSignal.asInstanceOf[ServerSentEvent[String]]
        val result      = event.encode

        assertTrue(
          result ==
            """|data: signals {"foo":null,"bar":null}
               |event: datastar-patch-signals
               |
               |""".stripMargin
        )
      },
      test("should encode with id and retry when using ServerSentEventGenerator") {
        val state          = TestState(foo = 1, bar = 2)
        val patchSignal    = PatchSignal(state)
        val event          = patchSignal.asInstanceOf[ServerSentEvent[String]]
        val withIdAndRetry = event.copy(id = Some("123"), retry = Some(zio.Duration.fromMillis(2000)))
        val result         = withIdAndRetry.encode

        assertTrue(
          result ==
            """|data: signals {"foo":1,"bar":2}
               |event: datastar-patch-signals
               |id: 123
               |retry: 2000
               |
               |""".stripMargin
        )
      }
    )
}
