package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}

import scala.meta.Defn.Class

object TestableEnrichedRegularClass {

  def apply(defnClass: Class,
            javaModifiers: List[JavaModifier] = Nil,
            javaTypeKeyword: JavaKeyword = JavaKeyword.Class,
            maybeInheritanceKeyword: Option[JavaKeyword] = None,
            enrichedStats: List[EnrichedStat] = Nil): EnrichedRegularClass =
    EnrichedRegularClass(
      scalaMods = defnClass.mods,
      javaModifiers = javaModifiers,
      javaTypeKeyword = javaTypeKeyword,
      name = defnClass.name,
      tparams = defnClass.tparams,
      ctor = defnClass.ctor,
      maybeInheritanceKeyword = maybeInheritanceKeyword,
      inits = defnClass.templ.inits,
      self = defnClass.templ.self,
      enrichedStats = enrichedStats
    )
}
