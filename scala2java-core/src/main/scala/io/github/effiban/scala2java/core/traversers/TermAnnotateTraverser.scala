package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

trait TermAnnotateTraverser extends ScalaTreeTraverser1[Term.Annotate]

private[traversers] class TermAnnotateTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                                    annotTraverser: => AnnotTraverser)extends TermAnnotateTraverser {

  // Expression annotation, e.g.:  (x: @annot) match ....
  // Partially supported in Java, so it will be rendered properly if it is a Java annotation
  override def traverse(termAnnotation: Term.Annotate): Term.Annotate = {
    val traversedExpr = expressionTermTraverser.traverse(termAnnotation.expr)
    val traversedAnnots = termAnnotation.annots.map(annotTraverser.traverse)
    termAnnotation.copy(traversedExpr, traversedAnnots)
  }
}
