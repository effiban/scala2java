package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Mod.Annot

trait AnnotTraverser extends ScalaTreeTraverser[Annot]

object AnnotTraverser extends AnnotTraverser {

  override def traverse(annotation: Annot): Unit = {
    emit("@")
    InitTraverser.traverse(annotation.init)
  }
}
