package zio.datastar.poc.states

import zio.datastar.poc.macros.datastarFieldName
import zio.json.JsonCodec
import zio.schema.{Schema, derived}

final case class CounterState(count: Int) derives Schema, JsonCodec
object CounterState {
  val initial: CounterState = CounterState(count = 0)

  // Field name extraction using macro
  val countField: String = datastarFieldName[CounterState](_.count)
}
