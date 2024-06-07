package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext
import io.github.effiban.scala2java.spi.entities.QualifiedTermApply
import io.github.effiban.scala2java.spi.transformers.QualifiedTermApplyTransformer

class CompositeQualifiedTermApplyTransformer(override val coreTransformer: QualifiedTermApplyTransformer)
                                            (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeDifferentTypeTransformer1[QualifiedTermApply, TermApplyTransformationContext, QualifiedTermApply]
    with ExtensionAndCoreTransformers[QualifiedTermApplyTransformer]
    with QualifiedTermApplyTransformer {

  override protected val extensionTransformers: List[QualifiedTermApplyTransformer] = extensionRegistry.qualifiedTermApplyTransformers
}