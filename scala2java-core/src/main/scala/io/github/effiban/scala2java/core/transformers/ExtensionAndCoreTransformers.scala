package io.github.effiban.scala2java.core.transformers

trait ExtensionAndCoreTransformers[T] {

  protected val extensionTransformers: List[T]

  protected val coreTransformer: T

  protected[transformers] final lazy val transformers: List[T] = extensionTransformers :+ coreTransformer
}
