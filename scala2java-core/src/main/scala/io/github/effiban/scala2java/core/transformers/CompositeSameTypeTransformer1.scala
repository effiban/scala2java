package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.transformers.SameTypeTransformer1

trait CompositeSameTypeTransformer1[T, A] extends SameTypeTransformer1[T, A] {

  protected val transformers: List[SameTypeTransformer1[T, A]]

  override def transform(obj: T, arg: A): T = transformers.foldLeft(obj)((anObj, transformer) => transformer.transform(anObj, arg))
}
