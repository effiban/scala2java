package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext
import io.github.effiban.scala2java.spi.entities.UnqualifiedTermApply
import io.github.effiban.scala2java.spi.transformers.UnqualifiedTermApplyTransformer

class CompositeUnqualifiedTermApplyTransformer(override val coreTransformer: UnqualifiedTermApplyTransformer)
                                              (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeDifferentTypeTransformer1[UnqualifiedTermApply, TermApplyTransformationContext, UnqualifiedTermApply]
    with ExtensionAndCoreTransformers[UnqualifiedTermApplyTransformer]
    with UnqualifiedTermApplyTransformer {

  override protected val extensionTransformers: List[UnqualifiedTermApplyTransformer] = extensionRegistry.unqualifiedTermApplyTransformers
}