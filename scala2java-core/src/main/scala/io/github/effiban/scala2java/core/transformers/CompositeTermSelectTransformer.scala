package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.{SameTypeTransformer, TermSelectTransformer}

import scala.meta.Term

class CompositeTermSelectTransformer(coreTermSelectTransformer: TermSelectTransformer)
                                    (implicit extensionRegistry: ExtensionRegistry) extends CompositeSameTypeTransformer[Term.Select]
  with TermSelectTransformer {

  override protected val transformers: List[SameTypeTransformer[Term.Select]] = extensionRegistry.termSelectTransformers

  override def transform(termSelect: Term.Select): Term.Select = {
    coreTermSelectTransformer.transform(super.transform(termSelect))
  }
}