package io.github.effiban.scala2java.core.traversers

import scala.meta.Tree

trait ScalaTreeTraverser2[I <: Tree, O <: Tree] {
  def traverse(tree: I): O
}
