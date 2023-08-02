package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Decl

case class EnrichedDeclDef(override val stat: Decl.Def, override val javaModifiers: List[JavaModifier] = Nil)
  extends EnrichedStatWithJavaModifiers
