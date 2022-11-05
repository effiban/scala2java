package io.github.effiban.scala2java.core.typeinference

import scala.meta.Type

trait TypeInferrer[T] {
  def infer(source: T): Option[Type]
}
