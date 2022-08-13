package effiban.scala2java.typeinference

import scala.meta.{Tree, Type}

trait TypeInferer[T <: Tree] {
  def infer(tree: T): Option[Type]
}
