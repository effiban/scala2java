package io.github.effiban.scala2java.core.desugarers

import scala.meta.Tree

trait DifferentTypeDesugarer[I <: Tree, O <: Tree] {
  def desugar(tree: I): O
}
