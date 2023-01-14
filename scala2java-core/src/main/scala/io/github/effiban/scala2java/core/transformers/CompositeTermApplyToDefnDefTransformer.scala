package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.{DifferentTypeTransformer0, TermApplyToDefnDefTransformer}

import scala.meta.{Defn, Term}

class CompositeTermApplyToDefnDefTransformer(implicit extensionRegistry: ExtensionRegistry)
  extends CompositeDifferentTypeTransformer0[Term.Apply, Defn.Def] with TermApplyToDefnDefTransformer {

  override protected val transformers: List[DifferentTypeTransformer0[Term.Apply, Defn.Def]] = extensionRegistry.termApplyToDefnDefTransformers
}
