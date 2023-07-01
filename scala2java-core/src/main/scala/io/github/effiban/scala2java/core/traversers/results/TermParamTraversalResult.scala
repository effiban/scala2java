package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Term

case class TermParamTraversalResult(override val tree: Term.Param, override val javaModifiers: List[JavaModifier] = Nil)
  extends WithJavaModifiersTraversalResult {
}
