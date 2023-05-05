package io.github.effiban.scala2java.core.traversers

import scala.meta.Type

trait TypeAnnotateTraverser extends ScalaTreeTraverser1[Type.Annotate]

private[traversers] class TypeAnnotateTraverserImpl(typeTraverser: => TypeTraverser) extends TypeAnnotateTraverser {

  // type with annotation, e.g.: T @annot
  override def traverse(annotatedType: Type.Annotate): Type.Annotate = {
    //TODO - restore after renderers have been extracted
    /**annotListTraverser.traverseAnnotations(annotations = annotatedType.annots, onSameLine = true)
    write(" ")*/
    annotatedType.copy(tpe = typeTraverser.traverse(annotatedType.tpe))
  }
}
