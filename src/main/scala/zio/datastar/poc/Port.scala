package zio.datastar.poc

import zio.datastar.poc.extensions.Extended
import zio.prelude.Assertion.{greaterThanOrEqualTo, lessThanOrEqualTo}
import zio.prelude.{Subtype, Validation}

type Port = Port.Type
object Port extends Subtype[Int] with Extended[Int] {
  inline override def assertion = greaterThanOrEqualTo(0) && lessThanOrEqualTo(65535)

  // noinspection ConvertibleToMethodValue
  def parse(s: String): Validation[String, Port] =
    Validation
      .fromOptionWith(s"Invalid port value: $s")(s.toIntOption)
      .flatMap(Port.make(_))
}
