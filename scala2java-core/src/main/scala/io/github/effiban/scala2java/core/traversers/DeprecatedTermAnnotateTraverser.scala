package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

@deprecated
trait DeprecatedTermAnnotateTraverser extends ScalaTreeTraverser[Term.Annotate]

@deprecated
private[traversers] class DeprecatedTermAnnotateTraverserImpl(annotListTraverser: => DeprecatedAnnotListTraverser,
                                                              expressionTermTraverser: => DeprecatedExpressionTermTraverser)
                                                             (implicit javaWriter: JavaWriter) extends DeprecatedTermAnnotateTraverser {

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
