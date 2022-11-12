package io.github.effiban.scala2java.spi

import io.github.effiban.scala2java.spi.predicates.ImporterExcludedPredicate
import io.github.effiban.scala2java.spi.providers.AdditionalImportersProvider

trait Scala2JavaExtension {

  def additionalImportersProvider(): AdditionalImportersProvider = AdditionalImportersProvider.Empty

  def importerExcludedPredicate(): ImporterExcludedPredicate = ImporterExcludedPredicate.None
}
