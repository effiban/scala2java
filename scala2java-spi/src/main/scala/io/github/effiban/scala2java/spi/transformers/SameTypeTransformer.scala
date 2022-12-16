package io.github.effiban.scala2java.spi.transformers

trait SameTypeTransformer[T] {
  def transform(obj: T): T
}
