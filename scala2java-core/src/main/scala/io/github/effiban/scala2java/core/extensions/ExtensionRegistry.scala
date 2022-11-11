package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.spi.providers.DefaultImportersProvider

case class ExtensionRegistry(extensions: List[Scala2JavaExtension]) {

  val defaultImportersProviders: List[DefaultImportersProvider] = extensions.map(_.defaultImportersProvider())

}
