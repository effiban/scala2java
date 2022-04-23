package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Type

object TypeAnnotateTraverser extends ScalaTreeTraverser[Type.Annotate] {

  // type with annotation, e.g.: T @annot
  def traverse(annotatedType: Type.Annotate): Unit = {
    AnnotListTraverser.traverseAnnotations(annotatedType.annots)
    emit(" ")
    TypeTraverser.traverse(annotatedType.tpe)
  }
}
