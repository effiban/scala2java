package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait TermAnnotateTraverser extends ScalaTreeTraverser[Term.Annotate]

private[traversers] class TermAnnotateTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                    expressionTermTraverser: => TermTraverser)
                                                   (implicit javaWriter: JavaWriter) extends TermAnnotateTraverser {

  import javaWriter._

  // Expression annotation, e.g.:  (x: @annot) match ....
  // Partially supported in Java, so it will be rendered properly if it is a Java annotation
  override def traverse(termAnnotation: Term.Annotate): Unit = {
    write("(")
    annotListTraverser.traverseAnnotations(termAnnotation.annots, onSameLine = true)
    expressionTermTraverser.traverse(termAnnotation.expr)
    write(")")
  }
}
