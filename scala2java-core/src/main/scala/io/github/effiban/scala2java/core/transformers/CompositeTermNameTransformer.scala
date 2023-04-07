package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.TermNameTransformer

import scala.meta.Term

class CompositeTermNameTransformer(override val coreTransformer: TermNameTransformer)
                                  (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeDifferentTypeTransformer0[Term.Name, Term]
    with ExtensionAndCoreTransformers[TermNameTransformer]
    with TermNameTransformer {

  override protected val extensionTransformers: List[TermNameTransformer] = extensionRegistry.termNameTransformers
}