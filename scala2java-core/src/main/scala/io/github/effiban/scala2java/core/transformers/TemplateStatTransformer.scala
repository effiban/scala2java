package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.transformers.{SameTypeTransformer0, TemplateTermApplyInfixToDefnTransformer, TemplateTermApplyToDefnTransformer}

import scala.meta.{Stat, Term}


trait TemplateStatTransformer extends SameTypeTransformer0[Stat]

class TemplateStatTransformerImpl(templateTermApplyInfixToDefnTransformer: TemplateTermApplyInfixToDefnTransformer,
                                  templateTermApplyToDefnTransformer: TemplateTermApplyToDefnTransformer) extends TemplateStatTransformer {

  override def transform(stat: Stat): Stat = {
    stat match {
      case termApplyInfix: Term.ApplyInfix => templateTermApplyInfixToDefnTransformer.transform(termApplyInfix).getOrElse(termApplyInfix)
      case termApply: Term.Apply => templateTermApplyToDefnTransformer.transform(termApply).getOrElse(termApply)
      case _ => stat
    }
  }
}
