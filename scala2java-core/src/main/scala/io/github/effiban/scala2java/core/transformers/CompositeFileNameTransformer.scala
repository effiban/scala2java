package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.FileNameTransformer

class CompositeFileNameTransformer(implicit extensionRegistry: ExtensionRegistry) extends FileNameTransformer {

  /**
   * If several extensions change the name, the transformations will be applied in encounter order (the order in which the extensions are discovered).<br>
   * Therefore the resulting name might not be deterministic.<br>
   * It is the responsibility of the user to understand the consequences of applying multiple extensions.
   */
  override def transform(fileName: String): String = {
    extensionRegistry.fileNameTransformers
      .foldLeft(fileName)((aFileName, transformer) => transformer.transform(aFileName))
  }
}
