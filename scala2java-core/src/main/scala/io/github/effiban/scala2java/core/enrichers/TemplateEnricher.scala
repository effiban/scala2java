package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.classifiers.InitClassifier
import io.github.effiban.scala2java.core.enrichers.contexts.TemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedTemplate
import io.github.effiban.scala2java.core.resolvers.JavaInheritanceKeywordResolver
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.{Init, Template}

trait TemplateEnricher {

  def enrich(template: Template, context: TemplateEnrichmentContext = TemplateEnrichmentContext()): EnrichedTemplate
}

private[enrichers] class TemplateEnricherImpl(templateBodyEnricher: => TemplateBodyEnricher,
                                              javaInheritanceKeywordResolver: JavaInheritanceKeywordResolver,
                                              initClassifier: InitClassifier) extends TemplateEnricher {

  def enrich(template: Template, context: TemplateEnrichmentContext = TemplateEnrichmentContext()): EnrichedTemplate = {
    val nonEnumInits = template.inits.filterNot(initClassifier.isEnum)
    val maybeInheritanceKeyword = resolveInheritanceKeyword(nonEnumInits, context.javaScope)
    val enrichedBody = templateBodyEnricher.enrich(statements = template.stats, context = context)

    EnrichedTemplate(
      maybeInheritanceKeyword,
      nonEnumInits,
      template.self,
      enrichedBody.enrichedStats
    )
  }

  private def resolveInheritanceKeyword(inits: List[Init], javaScope: JavaScope) = {
    if (inits.nonEmpty) Some(javaInheritanceKeywordResolver.resolve(javaScope, inits)) else None
  }
}
