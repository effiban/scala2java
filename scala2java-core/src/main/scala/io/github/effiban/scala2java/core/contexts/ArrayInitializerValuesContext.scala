package io.github.effiban.scala2java.core.contexts

import scala.meta.{Term, Type}

case class ArrayInitializerValuesContext(maybeType: Option[Type] = None, values: List[Term] = Nil)
