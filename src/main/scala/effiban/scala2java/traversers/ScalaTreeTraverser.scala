package effiban.scala2java.traversers

import scala.meta.Tree

trait ScalaTreeTraverser[T <: Tree] {
  def traverse(tree: T): Unit
}
