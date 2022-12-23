package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.transformers.SameTypeTransformer

trait CompositeSameTypeWithCoreTransformer[T] extends CompositeSameTypeTransformer[T] {

  protected val otherTransformers: List[SameTypeTransformer[T]]

  protected val coreTransformer: SameTypeTransformer[T]

  protected final lazy val transformers: List[SameTypeTransformer[T]] = otherTransformers :+ coreTransformer
}
