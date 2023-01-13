package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.transformers.SameTypeTransformer0

trait CompositeSameTypeTransformer0[T] extends SameTypeTransformer0[T] {

  protected val transformers: List[SameTypeTransformer0[T]]

  override def transform(obj: T): T = transformers.foldLeft(obj)((anObj, transformer) => transformer.transform(anObj))
}
