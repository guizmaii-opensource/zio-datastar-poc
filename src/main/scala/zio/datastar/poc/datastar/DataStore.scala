package zio.datastar.poc.datastar

import zio.*
import zio.json.*
import zio.json.ast.Json

final class DataStore {
  private val signals: Ref.Synchronized[Map[String, Json]] = Ref.Synchronized.unsafe.make(Map.empty)(using Unsafe.unsafe)

  def get(key: String): UIO[Option[Json]] =
    signals.get.map(_.get(key))

  def put(key: String, value: Json): UIO[Unit] =
    signals.update(_ + (key -> value))

  def putAll(values: Map[String, Json]): UIO[Unit] =
    signals.update(_ ++ values)

  def remove(key: String): UIO[Unit] =
    signals.update(_ - key)

  def update(key: String, f: Option[Json] => Json): UIO[Unit] =
    signals.update(map => map + (key -> f(map.get(key))))

  def updateWith(key: String, f: Option[Json] => Option[Json]): UIO[Unit] =
    signals.update { map =>
      f(map.get(key)) match {
        case Some(newValue) => map + (key -> newValue)
        case None           => map - key
      }
    }

  def merge(patch: Map[String, Json]): UIO[Unit] =
    signals.update { current =>
      patch.foldLeft(current) { case (acc, (key, value)) =>
        if (value == Json.Null) acc - key
        else acc + (key -> value)
      }
    }

  def clear: UIO[Unit] =
    signals.set(Map.empty)

  def toMap: UIO[Map[String, Json]] =
    signals.get

  def toJson: UIO[Json] =
    signals.get.map(fields => Json.Obj(Chunk.fromIterable(fields)))
}

object DataStore {
  def make: UIO[DataStore] = ZIO.succeed(new DataStore)

  val live: ULayer[DataStore] = ZLayer.fromZIO(make)
}
