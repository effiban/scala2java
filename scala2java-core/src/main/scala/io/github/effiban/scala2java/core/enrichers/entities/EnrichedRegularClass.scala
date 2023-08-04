package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}

import scala.meta.{Ctor, Defn, Init, Mod, Name, Self, Template, Type}

case class EnrichedRegularClass(scalaMods: List[Mod] = Nil,
                                javaModifiers: List[JavaModifier] = Nil,
                                javaTypeKeyword: JavaKeyword = JavaKeyword.Class,
                                name: Type.Name,
                                tparams: List[Type.Param] = Nil,
                                ctor: Ctor.Primary,
                                maybeInheritanceKeyword: Option[JavaKeyword] = None,
                                inits: List[Init] = Nil,
                                self: Self = Self(Name.Anonymous(), None),
                                enrichedStats: List[EnrichedStat] = Nil) extends EnrichedDefn {
  override val stat: Defn.Class = Defn.Class(
    mods = scalaMods,
    name = name,
    tparams = tparams,
    ctor = ctor,
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
