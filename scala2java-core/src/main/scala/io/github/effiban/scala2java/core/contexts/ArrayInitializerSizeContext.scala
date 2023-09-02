package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.TypeSelects.ScalaAny

import scala.meta.{Lit, Term, Type}

case class ArrayInitializerSizeContext(tpe: Type = ScalaAny, size: Term = Lit.Int(0))
