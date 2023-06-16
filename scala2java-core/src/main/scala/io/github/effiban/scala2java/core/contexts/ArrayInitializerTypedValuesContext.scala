package io.github.effiban.scala2java.core.contexts

import scala.meta.{Term, Type}

case class ArrayInitializerTypedValuesContext(tpe: Type, values: List[Term] = Nil)
