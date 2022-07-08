package effiban.scala2java

import scala.meta.Term

trait TermAnnotateTraverser extends ScalaTreeTraverser[Term.Annotate]

private[scala2java] class TermAnnotateTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                    termTraverser: => TermTraverser)
                                                   (implicit javaEmitter: JavaEmitter) extends TermAnnotateTraverser {

  import javaEmitter._

  // Expression annotation, e.g.:  (x: @annot) match ....
  // Partially supported in Java, so it will be rendered properly if it is a Java annotation
  override def traverse(termAnnotation: Term.Annotate): Unit = {
    emit("(")
    annotListTraverser.traverseAnnotations(termAnnotation.annots, onSameLine = true)
    termTraverser.traverse(termAnnotation.expr)
    emit(")")
  }
}

object TermAnnotateTraverser extends TermAnnotateTraverserImpl(AnnotListTraverser, TermTraverser)
