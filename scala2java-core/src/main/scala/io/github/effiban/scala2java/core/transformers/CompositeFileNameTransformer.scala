package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.{FileNameTransformer, SameTypeTransformer}

class CompositeFileNameTransformer(implicit extensionRegistry: ExtensionRegistry) extends CompositeSameTypeTransformer[String]
  with FileNameTransformer {

  override protected val transformers: List[SameTypeTransformer[String]] = extensionRegistry.fileNameTransformers
}
