package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.transformers.{DifferentTypeTransformer0, TemplateTermApplyInfixToDefnTransformer}

import scala.meta.{Defn, Term}

class CompositeTemplateTermApplyInfixToDefnTransformer(implicit extensionRegistry: ExtensionRegistry)
  extends CompositeDifferentTypeTransformer0[Term.ApplyInfix, Defn] with TemplateTermApplyInfixToDefnTransformer {

  override protected val transformers: List[DifferentTypeTransformer0[Term.ApplyInfix, Defn]] =
    extensionRegistry.templateTermApplyInfixToDefnTransformers
}
