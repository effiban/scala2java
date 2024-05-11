package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.TermSelectTransformer

import scala.meta.Term

class CompositeTermSelectTransformer(override val coreTransformer: TermSelectTransformer)
                                    (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeDifferentTypeTransformer0[Term.Select, Term]
    with ExtensionAndCoreTransformers[TermSelectTransformer]
    with TermSelectTransformer {

  override protected val extensionTransformers: List[TermSelectTransformer] = extensionRegistry.termSelectTransformers
}