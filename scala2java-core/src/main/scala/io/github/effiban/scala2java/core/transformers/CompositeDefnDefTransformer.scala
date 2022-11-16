package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.DefnDefTransformer

import scala.meta.Defn

class CompositeDefnDefTransformer(implicit extensionRegistry: ExtensionRegistry) extends DefnDefTransformer {

  /**
   * If several extensions transform the same method, the transformations will be applied in encounter order (the order in which the extensions are discovered).<br>
   * Therefore the output method might not be deterministic.<br>
   * It is the responsibility of the user to understand the consequences of applying multiple extensions.
   */
  override def transform(defnDef: Defn.Def): Defn.Def = {
    extensionRegistry.defnDefTransformers
      .foldLeft(defnDef)((defnDef, transformer) => transformer.transform(defnDef))
  }
}
