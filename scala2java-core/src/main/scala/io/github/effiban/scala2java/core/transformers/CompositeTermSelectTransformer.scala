package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.TermSelectTransformer

import scala.meta.Term

class CompositeTermSelectTransformer(override val coreTransformer: TermSelectTransformer)
                                    (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeSameTypeTransformer0[Term.Select]
    with CoreAndOtherTransformers[TermSelectTransformer]
    with TermSelectTransformer {

  override protected val otherTransformers: List[TermSelectTransformer] = extensionRegistry.termSelectTransformers
}