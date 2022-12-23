package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.{SameTypeTransformer, TermApplyTransformer}

import scala.meta.Term

class CompositeTermApplyTransformer(override val coreTransformer: TermApplyTransformer)
                                   (implicit extensionRegistry: ExtensionRegistry) extends CompositeSameTypeWithCoreTransformer[Term.Apply]
 with TermApplyTransformer {

  override protected val otherTransformers: List[SameTypeTransformer[Term.Apply]] = extensionRegistry.termApplyTransformers
}