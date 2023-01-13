package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.transformers.{DefnValTransformer, SameTypeTransformer1}

import scala.meta.Defn

class CompositeDefnValTransformer(implicit val extensionRegistry: ExtensionRegistry)
  extends CompositeSameTypeTransformer1[Defn.Val, JavaScope] with DefnValTransformer {

  override val transformers: List[SameTypeTransformer1[Defn.Val, JavaScope]] = extensionRegistry.defnValTransformers
}
