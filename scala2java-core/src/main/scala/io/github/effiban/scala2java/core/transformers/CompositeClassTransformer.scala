package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.{ClassTransformer, SameTypeTransformer0}

import scala.meta.Defn

class CompositeClassTransformer(implicit extensionRegistry: ExtensionRegistry) extends CompositeSameTypeTransformer0[Defn.Class]
  with ClassTransformer {

  override protected val transformers: List[SameTypeTransformer0[Defn.Class]] = extensionRegistry.classTransformers
}
