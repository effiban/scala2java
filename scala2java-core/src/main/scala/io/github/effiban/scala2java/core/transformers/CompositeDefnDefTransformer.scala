package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.{DefnDefTransformer, SameTypeTransformer0}

import scala.meta.Defn

class CompositeDefnDefTransformer(implicit val extensionRegistry: ExtensionRegistry) extends CompositeSameTypeTransformer0[Defn.Def]
  with DefnDefTransformer {

  override protected val transformers: List[SameTypeTransformer0[Defn.Def]] = extensionRegistry.defnDefTransformers
}
