package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Defn.Trait
import scala.meta.{Ctor, Init, Mod, Name, Self, Template, Type}

case class EnrichedTrait(scalaMods: List[Mod] = Nil,
                         javaModifiers: List[JavaModifier] = Nil,
                         name: Type.Name,
                         tparams: List[Type.Param] = Nil,
                         inits: List[Init] = Nil,
                         self: Self = Self(Name.Anonymous(), None),
                         enrichedStats: List[EnrichedStat] = Nil) extends EnrichedDefn {
  override val stat: Trait = Trait(
    mods = scalaMods,
    name = name,
    tparams = tparams,
    ctor = Ctor.Primary(mods = Nil, name = Name.Anonymous(), paramss = List(Nil)),
    templ = Template(
      early = Nil,
      inits = inits,
      self = self,
      stats = enrichedStats.map(_.stat)
    )
  )

  val enrichedTemplate: EnrichedTemplate = EnrichedTemplate(
    inits = inits,
    self = self,
    enrichedStats = enrichedStats
  )
}
