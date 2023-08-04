package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}

import scala.meta.{Defn, Init, Mod, Name, Self, Template, Term}

case class EnrichedObject(scalaMods: List[Mod] = Nil,
                          javaModifiers: List[JavaModifier] = Nil,
                          javaTypeKeyword: JavaKeyword = JavaKeyword.Class,
                          name: Term.Name,
                          maybeInheritanceKeyword: Option[JavaKeyword] = None,
                          inits: List[Init] = Nil,
                          self: Self = Self(Name.Anonymous(), None),
                          enrichedStats: List[EnrichedStat] = Nil) extends EnrichedDefn {
  override val stat: Defn.Object = Defn.Object(
    mods = scalaMods,
    name = name,
    templ = Template(
      early = Nil,
      inits = inits,
      self = self,
      stats = enrichedStats.map(_.stat)
    )
  )

  val enrichedTemplate: EnrichedTemplate = EnrichedTemplate(
    maybeInheritanceKeyword = maybeInheritanceKeyword,
    inits = inits,
    self = self,
    enrichedStats = enrichedStats
  )
}
