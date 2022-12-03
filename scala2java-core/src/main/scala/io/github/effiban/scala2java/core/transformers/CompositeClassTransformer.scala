package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.ClassTransformer

import scala.meta.Defn

class CompositeClassTransformer(implicit extensionRegistry: ExtensionRegistry) extends ClassTransformer {

  /**
   * If several extensions transform the class, the transformations will be applied in encounter order (the order in which the extensions are discovered).<br>
   * Therefore the resulting name might not be deterministic.<br>
   * It is the responsibility of the user to understand the consequences of applying multiple extensions.
   */
  override def transform(classDef: Defn.Class): Defn.Class = {
    extensionRegistry.classTransformers
      .foldLeft(classDef)((aClassDef, transformer) => transformer.transform(aClassDef))
  }
}
