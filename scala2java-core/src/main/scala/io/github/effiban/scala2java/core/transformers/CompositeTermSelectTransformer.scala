package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.TermSelectTransformer

import scala.meta.Term

class CompositeTermSelectTransformer(coreTermSelectTransformer: TermSelectTransformer)
                                    (implicit extensionRegistry: ExtensionRegistry) extends TermSelectTransformer {

  override def transform(termSelect: Term.Select): Term.Select = {
    val transformedTermSelect = transformByExtensions(termSelect)
    coreTermSelectTransformer.transform(transformedTermSelect)
  }

  /**
   * If several extensions transform the 'Term.Select' - they will applied in encounter order.<br>
   * Therefore the output might not be deterministic.<br>
   * It is the responsibility of the user to understand the consequences of applying multiple extensions.
   */
  private def transformByExtensions(termSelect: Term.Select): Term.Select = {
    extensionRegistry.termSelectTransformers
      .foldLeft[Term.Select](termSelect)((aTermSelect, transformer) => transformer.transform(aTermSelect))
  }


}