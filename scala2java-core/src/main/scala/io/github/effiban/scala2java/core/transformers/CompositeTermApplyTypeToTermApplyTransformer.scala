package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.TermApplyTypeToTermApplyTransformer

import scala.meta.Term

class CompositeTermApplyTypeToTermApplyTransformer(implicit extensionRegistry: ExtensionRegistry)
  extends TermApplyTypeToTermApplyTransformer {

  /**
   * If several extensions transform the 'ApplyType' - they will applied in encounter order, until one returns a non-empty result (if any).<br>
   * Therefore the output might not be deterministic.<br>
   * It is the responsibility of the user to understand the consequences of applying multiple extensions.
   */
  override def transform(termApplyType: Term.ApplyType): Option[Term.Apply] = {
    extensionRegistry.termApplyTypeToTermApplyTransformers
      .foldLeft[Option[Term.Apply]](None)((maybeTermApply, transformer) => maybeTermApply.orElse(transformer.transform(termApplyType)))
  }
}
