package io.github.effiban.scala2java.core.transformers

trait CoreAndOtherTransformers[T] {

  protected val otherTransformers: List[T]

  protected val coreTransformer: T

  protected[transformers] final lazy val transformers: List[T] = otherTransformers :+ coreTransformer
}
