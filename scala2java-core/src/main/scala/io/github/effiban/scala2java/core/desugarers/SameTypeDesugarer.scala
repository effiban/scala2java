package io.github.effiban.scala2java.core.desugarers

import scala.meta.Tree

trait SameTypeDesugarer[T <: Tree] {
  def desugar(tree: T): T
}
