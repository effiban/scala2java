package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Type

trait TypeAnnotateTraverser extends ScalaTreeTraverser[Type.Annotate]

private[scala2java] class TypeAnnotateTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                    typeTraverser: => TypeTraverser) extends TypeAnnotateTraverser {

  // type with annotation, e.g.: T @annot
  override def traverse(annotatedType: Type.Annotate): Unit = {
    annotListTraverser.traverseAnnotations(annotatedType.annots)
    emit(" ")
    typeTraverser.traverse(annotatedType.tpe)
  }
}

object TypeAnnotateTraverser extends TypeAnnotateTraverserImpl(AnnotListTraverser, TypeTraverser)
