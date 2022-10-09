package io.github.effiban.scala2java.contexts

import io.github.effiban.scala2java.entities.TypeNameValues

import scala.meta.{Lit, Term, Type}

case class ArrayInitializerSizeContext(tpe: Type = Type.Name(TypeNameValues.ScalaAny), size: Term = Lit.Int(0))
