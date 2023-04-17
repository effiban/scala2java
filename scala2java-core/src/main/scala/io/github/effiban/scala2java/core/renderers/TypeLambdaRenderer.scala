package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeLambdaRenderer extends JavaTreeRenderer[Type.Lambda]

private[renderers] class TypeLambdaRendererImpl(implicit javaWriter: JavaWriter) extends TypeLambdaRenderer {

  import javaWriter._

  override def render(lambdaType: Type.Lambda): Unit = {
    writeComment(lambdaType.toString())
  }
}
