package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypeAnnotateTraverser extends ScalaTreeTraverser[Type.Annotate]

private[traversers] class TypeAnnotateTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                    typeTraverser: => TypeTraverser)
                                                   (implicit javaWriter: JavaWriter) extends TypeAnnotateTraverser {

  import javaWriter._

  // type with annotation, e.g.: T @annot
  override def traverse(annotatedType: Type.Annotate): Unit = {
    annotListTraverser.traverseAnnotations(annotations = annotatedType.annots, onSameLine = true)
    write(" ")
    typeTraverser.traverse(annotatedType.tpe)
  }
}
