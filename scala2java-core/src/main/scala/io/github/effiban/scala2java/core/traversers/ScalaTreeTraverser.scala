package io.github.effiban.scala2java.core.traversers

import scala.meta.Tree

@deprecated
trait ScalaTreeTraverser[T <: Tree] {
  def traverse(tree: T): Unit
}
