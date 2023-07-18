package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Decl

case class DeclDefTraversalResult(tree: Decl.Def, javaModifiers: List[JavaModifier] = Nil) extends StatWithJavaModifiersTraversalResult
