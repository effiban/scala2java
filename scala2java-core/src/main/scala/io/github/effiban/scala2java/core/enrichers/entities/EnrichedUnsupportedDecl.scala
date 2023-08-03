package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Decl

case class EnrichedUnsupportedDecl(override val stat: Decl, override val javaModifiers: List[JavaModifier] = Nil)
  extends EnrichedDecl
