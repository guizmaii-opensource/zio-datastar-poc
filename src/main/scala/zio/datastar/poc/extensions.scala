package zio.datastar.poc

import zio.json.{JsonCodec, JsonDecoder, JsonEncoder}
import zio.prelude.{NewtypeCustom, Subtype}
import zio.schema.Schema

object extensions {

  trait Unsafe[A] {
    self: NewtypeCustom[A] =>
    final def unsafe(a: A): Type                 = wrap(a)
    final def unsafeAll[F[_]](fa: F[A]): F[Type] = fa.asInstanceOf[F[Type]]
  }

  trait Extended[A] extends Unsafe[A] {
    self: NewtypeCustom[A] =>

    given encoder(using JsonEncoder[A]): zio.json.JsonEncoder[Type] = derive
    given decoder(using JsonDecoder[A]): zio.json.JsonDecoder[Type] =
      JsonDecoder[A].mapOrFail(make(_).toEitherWith(_.head))
    given codec(using JsonCodec[A]): zio.json.JsonCodec[Type]       = JsonCodec(encoder, decoder)
    given schema(using Schema[A]): zio.schema.Schema[Type]          =
      Schema[A].transformOrFail(make(_).toEitherWith(_.head), t => Right(unwrap(t)))
  }

  extension [T <: Subtype[?]](t: T) {
    def unsafe[A](a: A)(using ev: T <:< Subtype[A]): t.Type                 = a.asInstanceOf[t.Type]
    def unsafeAll[A, F[_]](fa: F[A])(using ev: T <:< Subtype[A]): F[t.Type] = fa.asInstanceOf[F[t.Type]]
  }

}
