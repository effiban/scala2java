package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Tree

trait WithJavaModifiersTraversalResult {
  val tree: Tree
  val javaModifiers: List[JavaModifier]
}
