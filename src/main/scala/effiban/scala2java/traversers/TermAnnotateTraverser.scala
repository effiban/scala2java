package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term

trait TermAnnotateTraverser extends ScalaTreeTraverser[Term.Annotate]

private[scala2java] class TermAnnotateTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                    termTraverser: => TermTraverser)
                                                   (implicit javaWriter: JavaWriter) extends TermAnnotateTraverser {

  import javaWriter._

  // Expression annotation, e.g.:  (x: @annot) match ....
  // Partially supported in Java, so it will be rendered properly if it is a Java annotation
  override def traverse(termAnnotation: Term.Annotate): Unit = {
    write("(")
    annotListTraverser.traverseAnnotations(termAnnotation.annots, onSameLine = true)
    termTraverser.traverse(termAnnotation.expr)
    write(")")
  }
}

object TermAnnotateTraverser extends TermAnnotateTraverserImpl(AnnotListTraverser, TermTraverser)
