package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{TemplateBodyContext, TemplateContext}
import io.github.effiban.scala2java.core.resolvers.JavaInheritanceKeywordResolver
import io.github.effiban.scala2java.core.traversers.results.TemplateTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.predicates.TemplateInitExcludedPredicate

import scala.meta.{Init, Template}

trait TemplateTraverser {

  def traverse(template: Template, context: TemplateContext): TemplateTraversalResult
}

private[traversers] class TemplateTraverserImpl(initTraverser: => InitTraverser,
                                                selfTraverser: => SelfTraverser,
                                                templateBodyTraverser: => TemplateBodyTraverser,
                                                javaInheritanceKeywordResolver: JavaInheritanceKeywordResolver,
                                                templateInitExcludedPredicate: TemplateInitExcludedPredicate) extends TemplateTraverser {

  def traverse(template: Template, context: TemplateContext): TemplateTraversalResult = {
    val includedInits = template.inits.filterNot(templateInitExcludedPredicate)
    val maybeInheritanceKeyword = resolveInheritanceKeyword(includedInits, context.javaScope)
    val traversedInits = includedInits.map(initTraverser.traverse)
    val traversedSelf = selfTraverser.traverse(template.self)
    val bodyContext = TemplateBodyContext(
      javaScope = context.javaScope,
      maybeClassName = context.maybeClassName,
      maybePrimaryCtor = context.maybePrimaryCtor,
      inits = includedInits
    )
    val multiStatResult = templateBodyTraverser.traverse(statements = template.stats, context = bodyContext)

    TemplateTraversalResult(
      maybeInheritanceKeyword = maybeInheritanceKeyword,
      inits = traversedInits,
      self = traversedSelf,
      statResults = multiStatResult.statResults
    )
  }

  private def resolveInheritanceKeyword(inits: List[Init], javaScope: JavaScope) = {
    if (inits.nonEmpty) Some(javaInheritanceKeywordResolver.resolve(javaScope, inits)) else None
  }
}
