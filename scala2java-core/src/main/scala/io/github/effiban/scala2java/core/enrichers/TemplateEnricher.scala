package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.enrichers.contexts.TemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedTemplate
import io.github.effiban.scala2java.core.resolvers.JavaInheritanceKeywordResolver
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.{Init, Template}

trait TemplateEnricher {

  def enrich(template: Template, context: TemplateEnrichmentContext = TemplateEnrichmentContext()): EnrichedTemplate
}

private[enrichers] class TemplateEnricherImpl(templateBodyEnricher: => TemplateBodyEnricher,
                                              javaInheritanceKeywordResolver: JavaInheritanceKeywordResolver) extends TemplateEnricher {

  def enrich(template: Template, context: TemplateEnrichmentContext = TemplateEnrichmentContext()): EnrichedTemplate = {
    val maybeInheritanceKeyword = resolveInheritanceKeyword(template.inits, context.javaScope)
    val enrichedBody = templateBodyEnricher.enrich(statements = template.stats, context = context)

    EnrichedTemplate(
      maybeInheritanceKeyword,
      template.inits,
      template.self,
      enrichedBody.enrichedStats
    )
  }

  private def resolveInheritanceKeyword(inits: List[Init], javaScope: JavaScope) = {
    if (inits.nonEmpty) Some(javaInheritanceKeywordResolver.resolve(javaScope, inits)) else None
  }
}
