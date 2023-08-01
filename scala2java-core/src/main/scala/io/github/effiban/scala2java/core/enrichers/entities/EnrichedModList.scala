package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Mod

case class EnrichedModList(scalaMods: List[Mod] = Nil, javaModifiers: List[JavaModifier] = Nil)
  extends EnrichedWithJavaModifiers
