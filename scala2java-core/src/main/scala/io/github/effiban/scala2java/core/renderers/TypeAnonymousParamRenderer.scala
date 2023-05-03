package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeAnonymousParamRenderer extends JavaTreeRenderer[Type.AnonymousParam]

private[renderers] class TypeAnonymousParamRendererImpl(implicit javaWriter: JavaWriter) extends TypeAnonymousParamRenderer {

  import javaWriter._

  // Underscore in type param with variance modifier e.g. T[+_] (not sure about this, anyway ignoring the variance)
  override def render(ignored: Type.AnonymousParam): Unit = {
    write("?")
  }
}
