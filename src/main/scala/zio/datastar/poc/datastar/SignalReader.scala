package zio.datastar.poc.datastar

import zio.*
import zio.http.*
import zio.json.*
import zio.json.ast.Json

object SignalReader {
  private val DatastarSignalsHeader = "datastar-signals"

  def readSignals(request: Request): IO[String, Map[String, Json]] =
    request.headers.get(DatastarSignalsHeader) match {
      case Some(headerValue) =>
        ZIO.fromEither(headerValue.fromJson[Json]).flatMap {
          case Json.Obj(fields) => ZIO.succeed(fields.toMap)
          case _                => ZIO.fail("Invalid signals format: expected JSON object")
        }
      case None              => ZIO.succeed(Map.empty)
    }

  def readSignalsAs[A: JsonDecoder](request: Request): IO[String, A] =
    request.headers.get(DatastarSignalsHeader) match {
      case Some(headerValue) => ZIO.fromEither(headerValue.fromJson[A])
      case None              => ZIO.fail("No signals found in request")
    }

  def readSignalsInto(request: Request, store: DataStore): IO[String, Unit] =
    for {
      signals <- readSignals(request)
      _       <- store.merge(signals)
    } yield ()

  def hasSignals(request: Request): Boolean =
    request.headers.get(DatastarSignalsHeader).isDefined
}
