package com.effiban.scala2java

import scala.meta.Tree

trait ScalaTreeTraverser[T <: Tree] {
  def traverse(tree: T): Unit
}
