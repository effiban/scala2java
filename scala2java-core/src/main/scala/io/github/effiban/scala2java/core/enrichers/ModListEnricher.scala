package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.ModifiersContext
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedModList
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver

trait ModListEnricher {
  def enrich(modifiersContext: ModifiersContext): EnrichedModList
}

private[enrichers] class ModListEnricherImpl(javaModifiersResolver: JavaModifiersResolver) extends ModListEnricher {

  override def enrich(modifiersContext: ModifiersContext): EnrichedModList = {
    val javaModifiers = javaModifiersResolver.resolve(modifiersContext)
    EnrichedModList(modifiersContext.scalaMods, javaModifiers)
  }
}

object ModListEnricher extends ModListEnricherImpl(JavaModifiersResolver)
