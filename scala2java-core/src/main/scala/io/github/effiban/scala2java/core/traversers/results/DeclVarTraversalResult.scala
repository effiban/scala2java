package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Decl

@deprecated
case class DeclVarTraversalResult(override val tree: Decl.Var, override val javaModifiers: List[JavaModifier] = Nil)
  extends DeclTraversalResult {
}
