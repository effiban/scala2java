package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.transformers.DefnValToDeclVarTransformer

import scala.meta.{Decl, Defn}

class CompositeDefnValToDeclVarTransformer(implicit extensionRegistry: ExtensionRegistry) extends DefnValToDeclVarTransformer {

  /**
   * If several extensions transform the same 'Defn.Val', the transformations will be applied in encounter order, until one returns a non-empty result (if any)..<br>
   * Therefore the output method might not be deterministic.<br>
   * It is the responsibility of the user to understand the consequences of applying multiple extensions.
   */
  override def transform(defnVal: Defn.Val, javaScope: JavaScope): Option[Decl.Var] = {
    extensionRegistry.defnValToDeclVarTransformers
      .foldLeft[Option[Decl.Var]](None)((maybeDeclVar, transformer) => maybeDeclVar.orElse(transformer.transform(defnVal, javaScope)))
  }
}
