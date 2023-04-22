package io.github.effiban.scala2java.core.traversers

import scala.meta.Tree

trait ScalaTreeTraverser1[T <: Tree] {
  def traverse(tree: T): T
}
