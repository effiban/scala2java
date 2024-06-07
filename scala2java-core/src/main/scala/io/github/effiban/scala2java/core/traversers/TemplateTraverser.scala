package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{TemplateBodyContext, TemplateContext}

import scala.meta.Template

trait TemplateTraverser {

  def traverse(template: Template, context: TemplateContext): Template
}

private[traversers] class TemplateTraverserImpl(templateInitTraverser: => TemplateInitTraverser,
                                                selfTraverser: => SelfTraverser,
                                                templateBodyTraverser: => TemplateBodyTraverser) extends TemplateTraverser {

  def traverse(template: Template, context: TemplateContext): Template = {
    val traversedInits = template.inits.map(templateInitTraverser.traverse)
    val traversedSelf = selfTraverser.traverse(template.self)
    val bodyContext = TemplateBodyContext(
      javaScope = context.javaScope,
      maybeClassName = context.maybeClassName,
      maybePrimaryCtor = context.maybePrimaryCtor,
      inits = template.inits
    )
    val traversedStats = templateBodyTraverser.traverse(statements = template.stats, context = bodyContext)

    Template(
      // TODO handle early definitions
      early = Nil,
      inits = traversedInits,
      self = traversedSelf,
      stats = traversedStats
    )
  }
}
