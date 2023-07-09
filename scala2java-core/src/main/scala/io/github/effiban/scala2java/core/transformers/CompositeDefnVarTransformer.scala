package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.transformers.{DefnVarTransformer, SameTypeTransformer1}

import scala.meta.Defn

class CompositeDefnVarTransformer(implicit val extensionRegistry: ExtensionRegistry)
  extends CompositeSameTypeTransformer1[Defn.Var, JavaScope] with DefnVarTransformer {

  override val transformers: List[SameTypeTransformer1[Defn.Var, JavaScope]] = extensionRegistry.defnVarTransformers
}
