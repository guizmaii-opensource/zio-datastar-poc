package zio.datastar.poc

object macros {

  import scala.quoted.*

  inline def datastarFieldName[T](inline selector: T => Any): String =
    ${ datastarFieldNameImpl[T]('selector) }

  private def datastarFieldNameImpl[T: Type](selector: Expr[T => Any])(using Quotes): Expr[String] = {
    import quotes.reflect.*

    selector.asTerm match {
      case Inlined(_, _, Lambda(_, Select(_, name))) =>
        // Direct field access: _.field
        Expr("$" + name)
      case Lambda(_, Select(_, name))                =>
        // Direct field access without inlining
        Expr("$" + name)
      case term                                      =>
        // For debugging
        report.errorAndAbort(
          s"Unexpected structure. Expected _.fieldName, got AST: ${term.show(using Printer.TreeStructure)}"
        )
    }
  }

}
