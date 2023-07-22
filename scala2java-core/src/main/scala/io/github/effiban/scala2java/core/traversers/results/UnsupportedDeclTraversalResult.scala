package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Decl

case class UnsupportedDeclTraversalResult(override val tree: Decl, override val javaModifiers: List[JavaModifier] = Nil)
  extends DeclTraversalResult
