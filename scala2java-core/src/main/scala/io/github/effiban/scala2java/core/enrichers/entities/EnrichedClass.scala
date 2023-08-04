package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}

import scala.meta.{Ctor, Defn, Init, Mod, Self, Template, Type}

trait EnrichedClass extends EnrichedDefn {
  val scalaMods: List[Mod]

  val javaModifiers: List[JavaModifier]

  val name: Type.Name

  val tparams: List[Type.Param]

  val ctor: Ctor.Primary

  val maybeInheritanceKeyword: Option[JavaKeyword]

  val inits: List[Init]

  val self: Self

  val enrichedStats: List[EnrichedStat]

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
