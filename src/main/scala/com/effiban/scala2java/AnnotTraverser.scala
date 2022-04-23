package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Mod.Annot

object AnnotTraverser extends ScalaTreeTraverser[Annot] {

  override def traverse(annotation: Annot): Unit = {
    emit("@")
    GenericTreeTraverser.traverse(annotation.init)
  }
}
