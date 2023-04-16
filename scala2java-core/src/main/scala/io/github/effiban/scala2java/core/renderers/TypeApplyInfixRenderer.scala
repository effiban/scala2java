package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeApplyInfixRenderer extends JavaTreeRenderer[Type.ApplyInfix]

private[renderers] class TypeApplyInfixRendererImpl(implicit javaWriter: JavaWriter) extends TypeApplyInfixRenderer {

  import javaWriter._

  override def render(typeApplyInfix: Type.ApplyInfix): Unit = {
    //TODO should be removed once the corresponding traverser converts to a Type.Apply
    writeComment(typeApplyInfix.toString())
  }
}
