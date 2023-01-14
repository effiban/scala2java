package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.{DifferentTypeTransformer0, TermApplyToClassTransformer}

import scala.meta.{Defn, Term}

class CompositeTermApplyToClassTransformer(implicit extensionRegistry: ExtensionRegistry)
  extends CompositeDifferentTypeTransformer0[Term.Apply, Defn.Class] with TermApplyToClassTransformer {

  override protected val transformers: List[DifferentTypeTransformer0[Term.Apply, Defn.Class]] = extensionRegistry.termApplyToClassTransformers
}
