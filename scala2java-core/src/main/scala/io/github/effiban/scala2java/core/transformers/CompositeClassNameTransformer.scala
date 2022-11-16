package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.ClassNameTransformer

import scala.meta.Type

class CompositeClassNameTransformer(implicit extensionRegistry: ExtensionRegistry) extends ClassNameTransformer {

  /**
   * If several extensions change the name, the transformations will be applied in encounter order (the order in which the extensions are discovered).<br>
   * Therefore the resulting name might not be deterministic.<br>
   * It is the responsibility of the user to understand the consequences of applying multiple extensions.
   */
  override def transform(className: Type.Name): Type.Name = {
    extensionRegistry.classNameTransformers
      .foldLeft(className)((clsName, transformer) => transformer.transform(clsName))
  }
}
