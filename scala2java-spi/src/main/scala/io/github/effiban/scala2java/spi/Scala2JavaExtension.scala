package io.github.effiban.scala2java.spi

import io.github.effiban.scala2java.spi.predicates.ImporterIncludedPredicate
import io.github.effiban.scala2java.spi.providers.AdditionalImportersProvider

trait Scala2JavaExtension {

  def additionalImportersProvider(): AdditionalImportersProvider = AdditionalImportersProvider.Empty

  def importerIncludedPredicate(): ImporterIncludedPredicate = ImporterIncludedPredicate.All
}
