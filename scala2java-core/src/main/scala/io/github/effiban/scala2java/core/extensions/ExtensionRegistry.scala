package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.spi.providers.JavaImportersProvider

case class ExtensionRegistry(extensions: List[Scala2JavaExtension]) {

  val javaImportersProviders: List[JavaImportersProvider] = extensions.map(_.javaImportersProvider())

}
