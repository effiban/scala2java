package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{TemplateBodyContext, TemplateContext}
import io.github.effiban.scala2java.spi.predicates.TemplateInitExcludedPredicate

import scala.meta.Template

trait TemplateTraverser {

  def traverse(template: Template, context: TemplateContext): Template
}

private[traversers] class TemplateTraverserImpl(initTraverser: => InitTraverser,
                                                selfTraverser: => SelfTraverser,
                                                templateBodyTraverser: => TemplateBodyTraverser,
                                                templateInitExcludedPredicate: TemplateInitExcludedPredicate) extends TemplateTraverser {

  def traverse(template: Template, context: TemplateContext): Template = {
    val includedInits = template.inits.filterNot(templateInitExcludedPredicate)
    val traversedInits = includedInits.map(initTraverser.traverse)
    val traversedSelf = selfTraverser.traverse(template.self)
    val bodyContext = TemplateBodyContext(
      javaScope = context.javaScope,
      maybeClassName = context.maybeClassName,
      maybePrimaryCtor = context.maybePrimaryCtor,
      inits = includedInits
    )
    val multiStatResult = templateBodyTraverser.traverse(statements = template.stats, context = bodyContext)

    Template(
      early = Nil,
      inits = traversedInits,
      self = traversedSelf,
      stats = multiStatResult.statResults.map(_.tree)
    )
  }
}
