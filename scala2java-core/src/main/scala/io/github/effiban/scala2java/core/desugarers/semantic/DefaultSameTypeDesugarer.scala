package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.desugarers.SameTypeDesugarer

import scala.meta.Tree
import scala.reflect.ClassTag

private[semantic] class DefaultSameTypeDesugarer[T <: Tree: ClassTag](treeDesugarer: => TreeDesugarer)
  extends SameTypeDesugarer[T] {

  override def desugar(tree: T): T = {
    treeDesugarer.desugar(tree) match {
      case desugaredTree: T => desugaredTree
      case aRes => throw new IllegalStateException(s"The inner desugarer should return a Source, but it returned: $aRes")
    }
  }
}