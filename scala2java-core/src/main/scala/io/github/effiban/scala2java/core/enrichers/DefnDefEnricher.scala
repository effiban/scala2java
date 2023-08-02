package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedDefnDef
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver

import scala.meta.Defn

trait DefnDefEnricher {
  def enrich(defnDef: Defn.Def, context: StatContext = StatContext()): EnrichedDefnDef
}

private[enrichers] class DefnDefEnricherImpl(javaModifiersResolver: JavaModifiersResolver) extends DefnDefEnricher {

  override def enrich(defnDef: Defn.Def, context: StatContext = StatContext()): EnrichedDefnDef = {
    val javaModifiers = javaModifiersResolver.resolve(ModifiersContext(defnDef, JavaTreeType.Method, context.javaScope))
    EnrichedDefnDef(defnDef, javaModifiers)
  }
}

object DefnDefEnricher extends DefnDefEnricherImpl(JavaModifiersResolver)
