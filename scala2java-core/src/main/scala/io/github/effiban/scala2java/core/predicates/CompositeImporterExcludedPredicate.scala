package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.predicates.ImporterExcludedPredicate

import scala.meta.Importer

class CompositeImporterExcludedPredicate(coreImporterExcludedPredicate: ImporterExcludedPredicate)
                                        (implicit extensionRegistry: ExtensionRegistry) extends ImporterExcludedPredicate {

  override def apply(importer: Importer): Boolean = {
    val predicates = coreImporterExcludedPredicate +: extensionRegistry.importerExcludedPredicates
    predicates.forall(_.apply(importer))
  }
}
