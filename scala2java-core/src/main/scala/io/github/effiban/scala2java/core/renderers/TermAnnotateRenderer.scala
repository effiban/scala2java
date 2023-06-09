package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait TermAnnotateRenderer extends JavaTreeRenderer[Term.Annotate]

private[renderers] class TermAnnotateRendererImpl(annotListRenderer: => AnnotListRenderer,
                                                  expressionTermRenderer: => ExpressionTermRenderer)
                                                 (implicit javaWriter: JavaWriter) extends TermAnnotateRenderer {

  import javaWriter._

  // Expression annotation, e.g.:  (x: @annot) match ....
  // Partially supported in Java, so it will be rendered properly if it is a Java annotation
  override def render(termAnnotation: Term.Annotate): Unit = {
    write("(")
    annotListRenderer.render(termAnnotation.annots, onSameLine = true)
    expressionTermRenderer.render(termAnnotation.expr)
    write(")")
  }
}
