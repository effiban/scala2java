package io.github.effiban.scala2java.spi.transformers

trait DifferentTypeTransformer[I, O] {
  def transform(obj: I): Option[O]
}
