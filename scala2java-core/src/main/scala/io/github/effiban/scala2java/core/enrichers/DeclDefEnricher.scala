package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedDeclDef
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver

import scala.meta.Decl

trait DeclDefEnricher {
  def enrich(declDef: Decl.Def, context: StatContext = StatContext()): EnrichedDeclDef
}

private[enrichers] class DeclDefEnricherImpl(javaModifiersResolver: JavaModifiersResolver) extends DeclDefEnricher {

  override def enrich(declDef: Decl.Def, context: StatContext = StatContext()): EnrichedDeclDef = {
    val javaModifiers = javaModifiersResolver.resolve(ModifiersContext(declDef, JavaTreeType.Method, context.javaScope))
    EnrichedDeclDef(declDef, javaModifiers)
  }
}

object DeclDefEnricher extends DeclDefEnricherImpl(JavaModifiersResolver)
