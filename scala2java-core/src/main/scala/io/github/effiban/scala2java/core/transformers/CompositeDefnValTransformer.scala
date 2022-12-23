package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.transformers.DefnValTransformer

import scala.meta.Defn

class CompositeDefnValTransformer(implicit val extensionRegistry: ExtensionRegistry) extends DefnValTransformer {

  override def transform(defnVal: Defn.Val, javaScope: JavaScope): Defn.Val = {
    extensionRegistry.defnValTransformers
      .foldLeft[Defn.Val](defnVal)((aDefnVal, transformer) => transformer.transform(aDefnVal, javaScope))
  }
}
