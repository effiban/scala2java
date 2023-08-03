package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Defn

case class EnrichedUnsupportedDefn(override val stat: Defn, override val javaModifiers: List[JavaModifier] = Nil)
  extends EnrichedDefn
