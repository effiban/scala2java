package io.github.effiban.scala2java.spi

import io.github.effiban.scala2java.spi.predicates.ImporterIncludedPredicate
import io.github.effiban.scala2java.spi.providers.DefaultImportersProvider

trait Scala2JavaExtension {

  def defaultImportersProvider(): DefaultImportersProvider = DefaultImportersProvider.Empty

  def importerIncludedPredicate(): ImporterIncludedPredicate = ImporterIncludedPredicate.All
}
