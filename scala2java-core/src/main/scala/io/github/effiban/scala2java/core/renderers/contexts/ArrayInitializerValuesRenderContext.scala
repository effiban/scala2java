package io.github.effiban.scala2java.core.renderers.contexts

import io.github.effiban.scala2java.core.entities.TypeNames.JavaObject

import scala.meta.{Term, Type}

case class ArrayInitializerValuesRenderContext(tpe: Type = JavaObject, values: List[Term] = Nil)
