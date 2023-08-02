package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedDeclVar
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver

import scala.meta.Decl

trait DeclVarEnricher {
  def enrich(declVar: Decl.Var, context: StatContext = StatContext()): EnrichedDeclVar
}

private[enrichers] class DeclVarEnricherImpl(javaModifiersResolver: JavaModifiersResolver) extends DeclVarEnricher {

  override def enrich(declVar: Decl.Var, context: StatContext = StatContext()): EnrichedDeclVar = {
    val javaModifiers = javaModifiersResolver.resolve(ModifiersContext(declVar, JavaTreeType.Variable, context.javaScope))
    EnrichedDeclVar(declVar, javaModifiers)
  }
}

object DeclVarEnricher extends DeclVarEnricherImpl(JavaModifiersResolver)
