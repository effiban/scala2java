package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}

import scala.meta.{Ctor, Init, Mod, Name, Self, Type}

case class EnrichedCaseClass(override val scalaMods: List[Mod] = Nil,
                             override val javaModifiers: List[JavaModifier] = Nil,
                             override val name: Type.Name,
                             override val tparams: List[Type.Param] = Nil,
                             override val ctor: Ctor.Primary,
                             override val maybeInheritanceKeyword: Option[JavaKeyword] = None,
                             override val inits: List[Init] = Nil,
                             override val self: Self = Self(Name.Anonymous(), None),
                             override val enrichedStats: List[EnrichedStat] = Nil) extends EnrichedClass