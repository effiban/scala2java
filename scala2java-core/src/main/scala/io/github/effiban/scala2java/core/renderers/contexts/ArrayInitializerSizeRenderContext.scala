package io.github.effiban.scala2java.core.renderers.contexts

import io.github.effiban.scala2java.core.entities.TypeNames.JavaObject

import scala.meta.{Lit, Term, Type}

case class ArrayInitializerSizeRenderContext(tpe: Type = JavaObject, size: Term = Lit.Int(0))
