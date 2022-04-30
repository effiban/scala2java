package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Mod.Annot

trait AnnotTraverser extends ScalaTreeTraverser[Annot]

private[scala2java] class AnnotTraverserImpl(initTraverser: => InitTraverser) extends AnnotTraverser {

  override def traverse(annotation: Annot): Unit = {
    emit("@")
    initTraverser.traverse(annotation.init)
  }
}

object AnnotTraverser extends AnnotTraverserImpl(InitTraverser)