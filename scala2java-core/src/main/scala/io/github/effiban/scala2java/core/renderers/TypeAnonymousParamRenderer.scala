package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeAnonymousParamRenderer extends JavaTreeRenderer[Type.AnonymousParam]

private[renderers] class TypeAnonymousParamRendererImpl(implicit javaWriter: JavaWriter) extends TypeAnonymousParamRenderer {

  import javaWriter._

  override def render(ignored: Type.AnonymousParam): Unit = {
    write("?")
  }
}
