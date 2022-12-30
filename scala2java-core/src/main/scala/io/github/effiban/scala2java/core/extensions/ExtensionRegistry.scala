package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.Scala2JavaExtension

case class ExtensionRegistry(override val extensions: List[Scala2JavaExtension] = Nil)
  extends ExtensionContainer
    with ExtendedPredicates
    with ExtendedProviders
    with ExtendedTransformers
    with ExtendedTypeInferrers
