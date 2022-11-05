package io.github.effiban.scala2java.core.traversers

import scala.meta.Tree

trait ScalaTreeTraverser[T <: Tree] {
  def traverse(tree: T): Unit
}
