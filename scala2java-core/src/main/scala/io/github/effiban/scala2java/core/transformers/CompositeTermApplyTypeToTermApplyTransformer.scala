package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.{DifferentTypeTransformer, TermApplyTypeToTermApplyTransformer}

import scala.meta.Term

class CompositeTermApplyTypeToTermApplyTransformer(implicit extensionRegistry: ExtensionRegistry)
  extends CompositeDifferentTypeTransformer[Term.ApplyType, Term.Apply] with TermApplyTypeToTermApplyTransformer {

  override protected val transformers: List[DifferentTypeTransformer[Term.ApplyType, Term.Apply]] = extensionRegistry.termApplyTypeToTermApplyTransformers
}
