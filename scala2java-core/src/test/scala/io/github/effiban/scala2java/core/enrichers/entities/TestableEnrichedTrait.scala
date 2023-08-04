package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Defn.Trait

object TestableEnrichedTrait {

  def apply(defnTrait: Trait,
            javaModifiers: List[JavaModifier] = Nil,
            enrichedStats: List[EnrichedStat] = Nil): EnrichedTrait =
    EnrichedTrait(
      scalaMods = defnTrait.mods,
      javaModifiers = javaModifiers,
      name = defnTrait.name,
      tparams = defnTrait.tparams,
      inits = defnTrait.templ.inits,
      self = defnTrait.templ.self,
      enrichedStats = enrichedStats
    )
}
