package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.enrichers.contexts.TemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedTrait
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.Defn.Trait

trait TraitEnricher {
  def enrich(traitDef: Trait, context: StatContext = StatContext()): EnrichedTrait
}

private[enrichers] class TraitEnricherImpl(templateEnricher: => TemplateEnricher,
                                           javaModifiersResolver: JavaModifiersResolver) extends TraitEnricher {

  override def enrich(traitDef: Trait, context: StatContext = StatContext()): EnrichedTrait = {
    val javaModifiers = javaModifiersResolver.resolve(ModifiersContext(traitDef, JavaTreeType.Interface, context.javaScope))
    val templateContext = TemplateEnrichmentContext(javaScope = JavaScope.Interface)
    val enrichedTemplate = templateEnricher.enrich(traitDef.templ, templateContext)

    EnrichedTrait(
      scalaMods = traitDef.mods,
      javaModifiers = javaModifiers,
      name = traitDef.name,
      tparams = traitDef.tparams,
      inits = enrichedTemplate.inits,
      self = enrichedTemplate.self,
      enrichedStats = enrichedTemplate.enrichedStats
    )
  }
}
