package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.Scala2JavaExtension

private[extensions] trait ExtensionContainer {

  val extensions: List[Scala2JavaExtension]
}
