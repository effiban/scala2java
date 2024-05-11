package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext
import io.github.effiban.scala2java.spi.transformers.TermSelectNameTransformer

import scala.meta.Term

class CompositeTermSelectNameTransformer(override val coreTransformer: TermSelectNameTransformer)
                                        (implicit extensionRegistry: ExtensionRegistry)
  extends CompositeSameTypeTransformer1[Term.Name, TermSelectTransformationContext]
    with ExtensionAndCoreTransformers[TermSelectNameTransformer]
    with TermSelectNameTransformer {

  override protected val extensionTransformers: List[TermSelectNameTransformer] = extensionRegistry.termSelectNameTransformers
}