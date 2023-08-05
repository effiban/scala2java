package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}

import scala.meta.Defn

object TestableEnrichedObject {

  def apply(defnObject: Defn.Object,
            javaModifiers: List[JavaModifier] = Nil,
            javaTypeKeyword: JavaKeyword = JavaKeyword.Class,
            maybeInheritanceKeyword: Option[JavaKeyword] = None,
            enrichedStats: List[EnrichedStat] = Nil): EnrichedObject =
    EnrichedObject(
      scalaMods = defnObject.mods,
      javaModifiers = javaModifiers,
      javaTypeKeyword = javaTypeKeyword,
      name = defnObject.name,
      maybeInheritanceKeyword = maybeInheritanceKeyword,
      inits = defnObject.templ.inits,
      self = defnObject.templ.self,
      enrichedStats = enrichedStats
    )
}
