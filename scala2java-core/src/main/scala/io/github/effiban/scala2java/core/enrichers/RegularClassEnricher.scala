package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.enrichers.contexts.TemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedRegularClass
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaModifiersResolver, JavaTreeTypeResolver}

import scala.meta.Defn

trait RegularClassEnricher {
  def enrich(regularClass: Defn.Class, context: StatContext = StatContext()): EnrichedRegularClass
}

private[enrichers] class RegularClassEnricherImpl(templateEnricher: => TemplateEnricher,
                                                  javaModifiersResolver: JavaModifiersResolver,
                                                  javaTreeTypeResolver: JavaTreeTypeResolver,
                                                  javaChildScopeResolver: JavaChildScopeResolver) extends RegularClassEnricher {

  override def enrich(defnClass: Defn.Class, context: StatContext = StatContext()): EnrichedRegularClass = {
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(defnClass, defnClass.mods))
    val javaModifiers = javaModifiersResolver.resolve(ModifiersContext(defnClass, javaTreeType, context.javaScope))
    val javaChildScope = javaChildScopeResolver.resolve(JavaChildScopeContext(defnClass, javaTreeType))
    val templateContext = TemplateEnrichmentContext(javaScope = javaChildScope, maybeClassName = Some(defnClass.name))
    val enrichedTemplate = templateEnricher.enrich(defnClass.templ, templateContext)

    EnrichedRegularClass(
      scalaMods = defnClass.mods,
      javaModifiers = javaModifiers,
      name = defnClass.name,
      tparams = defnClass.tparams,
      ctor = defnClass.ctor,
      maybeInheritanceKeyword = enrichedTemplate.maybeInheritanceKeyword,
      inits = enrichedTemplate.inits,
      self = enrichedTemplate.self,
      enrichedStats = enrichedTemplate.enrichedStats
    )
  }
}
