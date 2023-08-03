package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Decl

case class EnrichedDeclVar(override val stat: Decl.Var, override val javaModifiers: List[JavaModifier] = Nil)
  extends EnrichedDecl
