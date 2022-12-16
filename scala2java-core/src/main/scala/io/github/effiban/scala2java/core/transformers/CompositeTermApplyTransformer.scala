package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.{SameTypeTransformer, TermApplyTransformer}

import scala.meta.Term

class CompositeTermApplyTransformer(coreTermApplyTransformer: TermApplyTransformer)
                                   (implicit extensionRegistry: ExtensionRegistry) extends CompositeSameTypeTransformer[Term.Apply]
 with TermApplyTransformer {

  override protected val transformers: List[SameTypeTransformer[Term.Apply]] = extensionRegistry.termApplyTransformers

  override def transform(termApply: Term.Apply): Term.Apply = {
    coreTermApplyTransformer.transform(super.transform(termApply))
  }
}