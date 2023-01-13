package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.{FileNameTransformer, SameTypeTransformer1}

import scala.meta.Init

class CompositeFileNameTransformer(implicit extensionRegistry: ExtensionRegistry) extends CompositeSameTypeTransformer1[String, List[Init]]
  with FileNameTransformer {

  override protected val transformers: List[SameTypeTransformer1[String, List[Init]]] = extensionRegistry.fileNameTransformers
}
