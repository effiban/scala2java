package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Pat
import scala.meta.Pat.Bind

trait BindRenderer extends JavaTreeRenderer[Pat.Bind]

private[renderers] class BindRendererImpl(implicit javaWriter: JavaWriter) extends BindRenderer {

  import javaWriter._

  // Pattern match bind variable, e.g.: a @ A().
  override def render(patternBind: Bind): Unit = {
    writeComment(patternBind.toString())
  }
}
