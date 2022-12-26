package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.{ImporterTransformer, SameTypeTransformer}

import scala.meta.Importer

class CompositeImporterTransformer(implicit val extensionRegistry: ExtensionRegistry) extends CompositeSameTypeTransformer[Importer]
  with ImporterTransformer {

  override protected val transformers: List[SameTypeTransformer[Importer]] = extensionRegistry.importerTransformers
}