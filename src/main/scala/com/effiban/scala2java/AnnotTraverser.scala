package com.effiban.scala2java

import scala.meta.Mod.Annot

trait AnnotTraverser extends ScalaTreeTraverser[Annot]

private[scala2java] class AnnotTraverserImpl(initTraverser: => InitTraverser)
                                            (implicit javaEmitter: JavaEmitter) extends AnnotTraverser {

  import javaEmitter._

  override def traverse(annotation: Annot): Unit = {
    emit("@")
    initTraverser.traverse(annotation.init)
  }
}

object AnnotTraverser extends AnnotTraverserImpl(InitTraverser)