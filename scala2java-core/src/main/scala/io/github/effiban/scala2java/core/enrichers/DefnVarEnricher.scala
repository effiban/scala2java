package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedDefnVar
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver

import scala.meta.Defn

trait DefnVarEnricher {
  def enrich(defnVar: Defn.Var, context: StatContext = StatContext()): EnrichedDefnVar
}

private[enrichers] class DefnVarEnricherImpl(javaModifiersResolver: JavaModifiersResolver) extends DefnVarEnricher {

  override def enrich(defnVar: Defn.Var, context: StatContext = StatContext()): EnrichedDefnVar = {
    val javaModifiers = javaModifiersResolver.resolve(ModifiersContext(defnVar, JavaTreeType.Variable, context.javaScope))
    EnrichedDefnVar(defnVar, javaModifiers)
  }
}

object DefnVarEnricher extends DefnVarEnricherImpl(JavaModifiersResolver)
