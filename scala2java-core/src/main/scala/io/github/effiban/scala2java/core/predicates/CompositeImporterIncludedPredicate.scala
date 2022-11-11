package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.predicates.ImporterIncludedPredicate

import scala.meta.Importer

class CompositeImporterIncludedPredicate(coreImporterIncludedPredicate: ImporterIncludedPredicate)
                                        (implicit extensionRegistry: ExtensionRegistry) extends ImporterIncludedPredicate {

  override def apply(importer: Importer): Boolean = {
    val predicates = coreImporterIncludedPredicate +: extensionRegistry.importerIncludedPredicates
    predicates.forall(_.apply(importer))
  }
}
