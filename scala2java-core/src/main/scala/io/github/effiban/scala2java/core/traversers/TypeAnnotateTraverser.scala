package io.github.effiban.scala2java.core.traversers

import scala.meta.Type

trait TypeAnnotateTraverser extends ScalaTreeTraverser[Type.Annotate]

private[traversers] class TypeAnnotateTraverserImpl(typeTraverser: => TypeTraverser) extends TypeAnnotateTraverser {

  // type with annotation, e.g.: T @annot
  override def traverse(annotatedType: Type.Annotate): Unit = {
    //TODO - restore after renderers have been extracted
    /**annotListTraverser.traverseAnnotations(annotations = annotatedType.annots, onSameLine = true)
    write(" ")*/
    typeTraverser.traverse(annotatedType.tpe)
  }
}
