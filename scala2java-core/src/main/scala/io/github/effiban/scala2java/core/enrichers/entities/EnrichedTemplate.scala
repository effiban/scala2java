package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.JavaKeyword

import scala.meta.{Init, Name, Self, Template}

case class EnrichedTemplate(maybeInheritanceKeyword: Option[JavaKeyword] = None,
                            inits: List[Init] = Nil,
                            self: Self = Self(Name.Anonymous(), None),
                            enrichedStats: List[EnrichedStat] = Nil) {
  val template: Template = Template(
    early = Nil,
    inits = inits,
    self = self,
    stats = enrichedStats.map(_.stat)
  )
}
