package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.TermApplyInfixToTermApplyTransformer

import scala.meta.Term

class CompositeTermApplyInfixToTermApplyTransformer(override protected val coreTransformer: TermApplyInfixToTermApplyTransformer)
                                                   (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeDifferentTypeTransformer0[Term.ApplyInfix, Term.Apply]
    with ExtensionAndCoreTransformers[TermApplyInfixToTermApplyTransformer]
    with TermApplyInfixToTermApplyTransformer {

  override protected val extensionTransformers: List[TermApplyInfixToTermApplyTransformer] = extensionRegistry.termApplyInfixToTermApplyTransformers
}
