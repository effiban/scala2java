package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.transformers.SameTypeTransformer

trait CompositeSameTypeTransformer[T] extends SameTypeTransformer[T] {

  protected val transformers: List[SameTypeTransformer[T]]
  override def transform(obj: T): T = transformers.foldLeft(obj)((anObj, transformer) => transformer.transform(anObj))
}
