package io.github.effiban.scala2java.core.desugarers

import scala.meta.Tree
import scala.reflect.ClassTag

private[desugarers] class DefaultSameTypeDesugarer[T <: Tree: ClassTag](treeDesugarer: => TreeDesugarer)
  extends SameTypeDesugarer[T] {

  override def desugar(tree: T): T = {
    treeDesugarer.desugar(tree) match {
      case desugaredTree: T => desugaredTree
      case aRes => throw new IllegalStateException(s"The inner desugarer should return a Source, but it returned: $aRes")
    }
  }
}