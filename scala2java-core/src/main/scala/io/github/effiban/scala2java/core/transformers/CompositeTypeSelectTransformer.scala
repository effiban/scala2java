package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.TypeSelectTransformer

import scala.meta.Type

class CompositeTypeSelectTransformer(override val coreTransformer: TypeSelectTransformer)
                                    (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeDifferentTypeTransformer0[Type.Select, Type.Ref]
    with ExtensionAndCoreTransformers[TypeSelectTransformer]
    with TypeSelectTransformer {

  override protected val extensionTransformers: List[TypeSelectTransformer] = extensionRegistry.typeSelectTransformers
}
