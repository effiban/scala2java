package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.TermApplyTransformer

import scala.meta.Term

class CompositeTermApplyTransformer(override val coreTransformer: TermApplyTransformer)
                                   (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeSameTypeTransformer0[Term.Apply]
    with ExtensionAndCoreTransformers[TermApplyTransformer]
    with TermApplyTransformer {

  override protected val extensionTransformers: List[TermApplyTransformer] = extensionRegistry.termApplyTransformers
}