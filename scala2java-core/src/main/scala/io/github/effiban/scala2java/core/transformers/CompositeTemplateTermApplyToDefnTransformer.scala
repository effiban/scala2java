package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.{DifferentTypeTransformer0, TemplateTermApplyToDefnTransformer}

import scala.meta.{Defn, Term}

class CompositeTemplateTermApplyToDefnTransformer(implicit extensionRegistry: ExtensionRegistry)
  extends CompositeDifferentTypeTransformer0[Term.Apply, Defn] with TemplateTermApplyToDefnTransformer {

  override protected val transformers: List[DifferentTypeTransformer0[Term.Apply, Defn]] = extensionRegistry.templateTermApplyToDefnTransformers
}
