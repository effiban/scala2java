package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.TypeNameTransformer

import scala.meta.Type

class CompositeTypeNameTransformer(override val coreTransformer: TypeNameTransformer)
                                  (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeSameTypeTransformer0[Type.Name]
    with ExtensionAndCoreTransformers[TypeNameTransformer]
    with TypeNameTransformer {

  override protected val extensionTransformers: List[TypeNameTransformer] = extensionRegistry.typeNameTransformers
}
