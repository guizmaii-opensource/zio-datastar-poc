package zio.datastar.poc.datastar

import zio.*
import zio.stream.*
import zio.http.*

final class ServerSentEventGenerator {
  private val eventIdCounter = Ref.unsafe.make(0)(using Unsafe.unsafe)

  def send[A](event: ServerSentEvent[A], customId: Option[String] = None, retryDuration: Option[Int] = None): UIO[ServerSentEvent[A]] =
    for {
      autoId <- eventIdCounter.updateAndGet(_ + 1)
      eventId = customId.orElse(Some(autoId.toString))
      retry   = retryDuration.map(d => zio.Duration.fromMillis(d.toLong)).orElse(Some(zio.Duration.fromMillis(1000)))
    } yield event.copy(id = eventId, retry = retry)

  def stream[A](events: UStream[ServerSentEvent[A]], customRetryDuration: Option[Int] = None): UStream[ServerSentEvent[A]] =
    events.mapZIO { event =>
      send(event, event.id, customRetryDuration.orElse(event.retry.map(_.toMillis.toInt)))
    }
}

object ServerSentEventGenerator {
  def make: UIO[ServerSentEventGenerator] = ZIO.succeed(new ServerSentEventGenerator)

  val live: ULayer[ServerSentEventGenerator] = ZLayer.fromZIO(make)
}
