package effiban.scala2java.contexts

import scala.meta.{Term, Type}

case class ArrayInitializerValuesContext(maybeType: Option[Type] = None, values: List[Term] = Nil)
