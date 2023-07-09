package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.transformers.{DefnVarToDeclVarTransformer, DifferentTypeTransformer1}

import scala.meta.{Decl, Defn}

class CompositeDefnVarToDeclVarTransformer(implicit extensionRegistry: ExtensionRegistry)
  extends CompositeDifferentTypeTransformer1[Defn.Var, JavaScope, Decl.Var] with DefnVarToDeclVarTransformer {

  protected val transformers: List[DifferentTypeTransformer1[Defn.Var, JavaScope, Decl.Var]] = extensionRegistry.defnVarToDeclVarTransformers
}
