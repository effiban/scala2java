package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Defn

case class EnrichedDefnDef(override val stat: Defn.Def, override val javaModifiers: List[JavaModifier] = Nil)
  extends EnrichedDefn
