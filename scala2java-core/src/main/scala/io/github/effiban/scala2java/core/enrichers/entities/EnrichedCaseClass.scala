package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}

import scala.meta.{Ctor, Init, Mod, Name, Self, Type}

case class EnrichedCaseClass(scalaMods: List[Mod] = Nil,
                             javaModifiers: List[JavaModifier] = Nil,
                             name: Type.Name,
                             tparams: List[Type.Param] = Nil,
                             ctor: Ctor.Primary,
                             maybeInheritanceKeyword: Option[JavaKeyword] = None,
                             inits: List[Init] = Nil,
                             self: Self = Self(Name.Anonymous(), None),
                             enrichedStats: List[EnrichedStat] = Nil) extends EnrichedClass