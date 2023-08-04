package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.enrichers.contexts.TemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedObject
import io.github.effiban.scala2java.core.entities.JavaTreeTypeToKeywordMapping
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaModifiersResolver, JavaTreeTypeResolver}

import scala.meta.Defn

trait ObjectEnricher {
  def enrich(defnObject: Defn.Object, context: StatContext = StatContext()): EnrichedObject
}

private[enrichers] class ObjectEnricherImpl(templateEnricher: => TemplateEnricher,
                                            javaModifiersResolver: JavaModifiersResolver,
                                            javaTreeTypeResolver: JavaTreeTypeResolver,
                                            javaChildScopeResolver: JavaChildScopeResolver) extends ObjectEnricher {

  override def enrich(defnObject: Defn.Object, context: StatContext = StatContext()): EnrichedObject = {
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(defnObject, defnObject.mods))
    val javaModifiers = javaModifiersResolver.resolve(ModifiersContext(defnObject, javaTreeType, context.javaScope))
    val javaChildScope = javaChildScopeResolver.resolve(JavaChildScopeContext(defnObject, javaTreeType))
    val javaTypeKeyword = JavaTreeTypeToKeywordMapping(javaTreeType)
    val templateContext = TemplateEnrichmentContext(javaScope = javaChildScope)
    val enrichedTemplate = templateEnricher.enrich(defnObject.templ, templateContext)

    EnrichedObject(
      scalaMods = defnObject.mods,
      javaModifiers = javaModifiers,
      javaTypeKeyword = javaTypeKeyword,
      name = defnObject.name,
      maybeInheritanceKeyword = enrichedTemplate.maybeInheritanceKeyword,
      inits = enrichedTemplate.inits,
      self = enrichedTemplate.self,
      enrichedStats = enrichedTemplate.enrichedStats
    )
  }
}
