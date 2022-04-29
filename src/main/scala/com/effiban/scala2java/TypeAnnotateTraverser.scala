package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Type

trait TypeAnnotateTraverser extends ScalaTreeTraverser[Type.Annotate]

object TypeAnnotateTraverser extends TypeAnnotateTraverser {

  // type with annotation, e.g.: T @annot
  override def traverse(annotatedType: Type.Annotate): Unit = {
    AnnotListTraverser.traverseAnnotations(annotatedType.annots)
    emit(" ")
    TypeTraverser.traverse(annotatedType.tpe)
  }
}
