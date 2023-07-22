package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Defn

case class DefnDefTraversalResult(tree: Defn.Def, javaModifiers: List[JavaModifier] = Nil) extends DefnTraversalResult
