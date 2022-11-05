package io.github.effiban.scala2java.core.providers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.providers.JavaImportersProvider

import scala.meta.Importer

class CompositeJavaImportersProvider(defaultProvider: JavaImportersProvider,
                                     extensionRegistry: ExtensionRegistry) extends JavaImportersProvider {

  override def provide(): List[Importer] = (defaultProvider +: extensionRegistry.javaImportersProviders).flatMap(_.provide())
}
