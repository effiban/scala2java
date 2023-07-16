package io.github.effiban.scala2java.core.renderers.contexts

import io.github.effiban.scala2java.core.entities.TypeNameValues.JavaObject

import scala.meta.{Lit, Term, Type}

case class ArrayInitializerSizeRenderContext(tpe: Type = Type.Name(JavaObject), size: Term = Lit.Int(0))
