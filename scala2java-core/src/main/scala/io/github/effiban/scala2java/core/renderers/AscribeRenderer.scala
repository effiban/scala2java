package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.Ascribe

trait AscribeRenderer extends JavaTreeRenderer[Ascribe]

private[renderers] class AscribeRendererImpl(typeRenderer: => TypeRenderer,
                                            expressionTermRenderer: => ExpressionTermRenderer)
                                           (implicit javaWriter: JavaWriter) extends AscribeRenderer {

  import javaWriter._

  // Explicitly specified type, e.g.: 2:Short
  // Java equivalent is casting. e.g. (short)2
  override def render(ascribe: Ascribe): Unit = {
    writeStartDelimiter(Parentheses)
    typeRenderer.render(ascribe.tpe)
    writeEndDelimiter(Parentheses)
    expressionTermRenderer.render(ascribe.expr)
  }
}
