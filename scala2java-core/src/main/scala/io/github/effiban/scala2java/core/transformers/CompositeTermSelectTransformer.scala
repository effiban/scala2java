package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.{SameTypeTransformer, TermSelectTransformer}

import scala.meta.Term

class CompositeTermSelectTransformer(override val coreTransformer: TermSelectTransformer)
                                    (implicit extensionRegistry: ExtensionRegistry) extends CompositeSameTypeWithCoreTransformer[Term.Select]
  with TermSelectTransformer {

  override protected val otherTransformers: List[SameTypeTransformer[Term.Select]] = extensionRegistry.termSelectTransformers
}