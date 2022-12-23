package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.{SameTypeTransformer, TypeNameTransformer}

import scala.meta.Type

class CompositeTypeNameTransformer(override val coreTransformer: TypeNameTransformer)
                                  (implicit extensionRegistry: ExtensionRegistry) extends CompositeSameTypeWithCoreTransformer[Type.Name]
  with TypeNameTransformer {

  override protected val otherTransformers: List[SameTypeTransformer[Type.Name]] = extensionRegistry.typeNameTransformers
}
