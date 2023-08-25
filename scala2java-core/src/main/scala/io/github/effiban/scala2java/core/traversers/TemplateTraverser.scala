package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{TemplateBodyContext, TemplateContext}
import io.github.effiban.scala2java.spi.predicates.TemplateInitExcludedPredicate

import scala.meta.Template

trait TemplateTraverser {

  def traverse(template: Template, context: TemplateContext): Template
}

private[traversers] class TemplateTraverserImpl(templateInitTraverser: => TemplateInitTraverser,
                                                selfTraverser: => SelfTraverser,
                                                templateBodyTraverser: => TemplateBodyTraverser,
                                                templateInitExcludedPredicate: TemplateInitExcludedPredicate) extends TemplateTraverser {

  def traverse(template: Template, context: TemplateContext): Template = {
    val includedInits = template.inits.filterNot(templateInitExcludedPredicate)
    val traversedInits = includedInits.map(templateInitTraverser.traverse)
    val traversedSelf = selfTraverser.traverse(template.self)
    val bodyContext = TemplateBodyContext(
      javaScope = context.javaScope,
      maybeClassName = context.maybeClassName,
      maybePrimaryCtor = context.maybePrimaryCtor,
      inits = includedInits
    )
    val traversedStats = templateBodyTraverser.traverse(statements = template.stats, context = bodyContext)

    Template(
      early = Nil,
      inits = traversedInits,
      self = traversedSelf,
      stats = traversedStats
    )
  }
}
