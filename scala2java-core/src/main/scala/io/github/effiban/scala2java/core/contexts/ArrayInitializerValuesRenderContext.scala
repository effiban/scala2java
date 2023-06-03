package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.TypeNameValues.JavaObject

import scala.meta.{Term, Type}

case class ArrayInitializerValuesRenderContext(tpe: Type = Type.Name(JavaObject), values: List[Term] = Nil)
