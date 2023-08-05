package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Decl

@deprecated
case class DeclDefTraversalResult(tree: Decl.Def, javaModifiers: List[JavaModifier] = Nil) extends DeclTraversalResult
