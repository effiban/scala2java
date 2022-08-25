package effiban.scala2java.typeinference

import scala.meta.Type

trait TypeInferrer[T] {
  def infer(source: T): Option[Type]
}
