package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentContext

import scala.meta.Tree

trait ArgumentTraverser[T <: Tree] {

  def traverse(arg: T, context: ArgumentContext): Unit
}
