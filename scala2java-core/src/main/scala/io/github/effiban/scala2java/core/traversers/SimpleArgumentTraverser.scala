package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentContext

import scala.meta.Tree

class SimpleArgumentTraverser[T <: Tree](treeTraverser: ScalaTreeTraverser[T]) extends ArgumentTraverser[T] {
  override def traverse(arg: T, context: ArgumentContext): Unit = treeTraverser.traverse(arg)
}
