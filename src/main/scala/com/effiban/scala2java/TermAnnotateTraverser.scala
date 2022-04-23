package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term

object TermAnnotateTraverser extends ScalaTreeTraverser[Term.Annotate] {

  // Expression annotation, e.g.:  (x: @annot) match ....
  // Partially supported in Java, so it will be rendered properly if it is a Java annotation
  def traverse(termAnnotation: Term.Annotate): Unit = {
    emit("(")
    AnnotListTraverser.traverseAnnotations(termAnnotation.annots, onSameLine = true)
    GenericTreeTraverser.traverse(termAnnotation.expr)
    emit(")")
  }
}
