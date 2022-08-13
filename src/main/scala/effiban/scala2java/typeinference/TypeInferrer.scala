package effiban.scala2java.typeinference

import scala.meta.{Tree, Type}

trait TypeInferrer[T <: Tree] {
  def infer(tree: T): Option[Type]
}
