package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.enrichers.contexts.TemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedCaseClass
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaModifiersResolver}

import scala.meta.Defn

trait CaseClassEnricher {
  def enrich(caseClass: Defn.Class, context: StatContext = StatContext()): EnrichedCaseClass
}

private[enrichers] class CaseClassEnricherImpl(templateEnricher: => TemplateEnricher,
                                               javaModifiersResolver: JavaModifiersResolver,
                                               javaChildScopeResolver: JavaChildScopeResolver) extends CaseClassEnricher {

  override def enrich(caseClass: Defn.Class, context: StatContext = StatContext()): EnrichedCaseClass = {
    val javaModifiers = javaModifiersResolver.resolve(ModifiersContext(caseClass, JavaTreeType.Record, context.javaScope))
    val javaChildScope = javaChildScopeResolver.resolve(JavaChildScopeContext(caseClass, JavaTreeType.Record))
    val templateContext = TemplateEnrichmentContext(javaScope = javaChildScope, maybeClassName = Some(caseClass.name))
    val enrichedTemplate = templateEnricher.enrich(caseClass.templ, templateContext)

    EnrichedCaseClass(
      scalaMods = caseClass.mods,
      javaModifiers = javaModifiers,
      name = caseClass.name,
      tparams = caseClass.tparams,
      ctor = caseClass.ctor,
      maybeInheritanceKeyword = enrichedTemplate.maybeInheritanceKeyword,
      inits = enrichedTemplate.inits,
      self = enrichedTemplate.self,
      enrichedStats = enrichedTemplate.enrichedStats
    )
  }
}
