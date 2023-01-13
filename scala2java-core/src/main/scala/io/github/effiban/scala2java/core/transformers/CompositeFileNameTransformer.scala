package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.{FileNameTransformer, SameTypeTransformer0}

class CompositeFileNameTransformer(implicit extensionRegistry: ExtensionRegistry) extends CompositeSameTypeTransformer0[String]
  with FileNameTransformer {

  override protected val transformers: List[SameTypeTransformer0[String]] = extensionRegistry.fileNameTransformers
}
