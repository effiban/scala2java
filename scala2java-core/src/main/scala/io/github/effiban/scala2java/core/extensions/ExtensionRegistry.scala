package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.spi.predicates.ImporterIncludedPredicate
import io.github.effiban.scala2java.spi.providers.AdditionalImportersProvider

case class ExtensionRegistry(extensions: List[Scala2JavaExtension]) {

  val additionalImportersProviders: List[AdditionalImportersProvider] = extensions.map(_.additionalImportersProvider())
  val importerIncludedPredicates: List[ImporterIncludedPredicate] = extensions.map(_.importerIncludedPredicate())

}
