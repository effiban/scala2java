package io.github.effiban.scala2java.core.providers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.providers.DefaultImportersProvider

import scala.meta.Importer

class CompositeDefaultImportersProvider(coreDefaultImportersProvider: DefaultImportersProvider)
                                       (implicit extensionRegistry: ExtensionRegistry) extends DefaultImportersProvider {

  override def provide(): List[Importer] = (coreDefaultImportersProvider +: extensionRegistry.defaultImportersProviders).flatMap(_.provide())
}
