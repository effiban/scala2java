package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.{DefnDefTransformer, SameTypeTransformer}

import scala.meta.Defn

class CompositeDefnDefTransformer(implicit val extensionRegistry: ExtensionRegistry) extends CompositeSameTypeTransformer[Defn.Def]
  with DefnDefTransformer {

  override protected val transformers: List[SameTypeTransformer[Defn.Def]] = extensionRegistry.defnDefTransformers
}
