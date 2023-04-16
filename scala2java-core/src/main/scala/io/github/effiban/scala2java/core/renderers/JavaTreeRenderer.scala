package io.github.effiban.scala2java.core.renderers

import scala.meta.Tree

trait JavaTreeRenderer[T <: Tree] {
  def render(tree: T): Unit
}
