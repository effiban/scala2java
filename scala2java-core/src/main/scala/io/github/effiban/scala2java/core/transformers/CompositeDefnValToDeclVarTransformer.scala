package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.transformers.{DefnValToDeclVarTransformer, DifferentTypeTransformer1}

import scala.meta.{Decl, Defn}

class CompositeDefnValToDeclVarTransformer(implicit extensionRegistry: ExtensionRegistry)
  extends CompositeDifferentTypeTransformer1[Defn.Val, JavaScope, Decl.Var] with DefnValToDeclVarTransformer {

  protected val transformers: List[DifferentTypeTransformer1[Defn.Val, JavaScope, Decl.Var]] = extensionRegistry.defnValToDeclVarTransformers
}
