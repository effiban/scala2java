package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.transformers.DefnValToDeclVarTransformer

import scala.meta.{Decl, Defn}

class CompositeDefnValToDeclVarTransformer(implicit extensionRegistry: ExtensionRegistry) extends DefnValToDeclVarTransformer {

  override def transform(defnVal: Defn.Val, javaScope: JavaScope): Option[Decl.Var] = {
    extensionRegistry.defnValToDeclVarTransformers
      .foldLeft[Option[Decl.Var]](None)((maybeDeclVar, transformer) => maybeDeclVar.orElse(transformer.transform(defnVal, javaScope)))
  }
}
