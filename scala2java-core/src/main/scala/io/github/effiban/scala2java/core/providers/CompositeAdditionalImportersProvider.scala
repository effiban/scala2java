package io.github.effiban.scala2java.core.providers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.providers.AdditionalImportersProvider

import scala.meta.Importer

class CompositeAdditionalImportersProvider(coreAdditionalImportersProvider: AdditionalImportersProvider)
                                          (implicit extensionRegistry: ExtensionRegistry) extends AdditionalImportersProvider {

  override def provide(): List[Importer] = (coreAdditionalImportersProvider +: extensionRegistry.additionalImportersProviders).flatMap(_.provide())
}
