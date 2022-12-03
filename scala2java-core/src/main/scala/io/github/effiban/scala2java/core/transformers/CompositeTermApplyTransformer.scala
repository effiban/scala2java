package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.TermApplyTransformer

import scala.meta.Term

class CompositeTermApplyTransformer(coreTermApplyTransformer: TermApplyTransformer)
                                   (implicit extensionRegistry: ExtensionRegistry) extends TermApplyTransformer {

  override def transform(termApply: Term.Apply): Term.Apply = {
    val transformedTermApply = transformByExtensions(termApply)
    coreTermApplyTransformer.transform(transformedTermApply)
  }

  /**
   * If several extensions transform the 'Term.Apply' - they will applied in encounter order.<br>
   * Therefore the output might not be deterministic.<br>
   * It is the responsibility of the user to understand the consequences of applying multiple extensions.
   */
  private def transformByExtensions(termApply: Term.Apply): Term.Apply = {
    extensionRegistry.termApplyTransformers
      .foldLeft[Term.Apply](termApply)((aTermApply, transformer) => transformer.transform(aTermApply))
  }


}