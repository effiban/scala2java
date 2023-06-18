package io.github.effiban.scala2java.core.traversers

import scala.meta.Mod.Annot

trait AnnotTraverser extends ScalaTreeTraverser1[Annot]

private[traversers] class AnnotTraverserImpl(initTraverser: => InitTraverser) extends AnnotTraverser {

  override def traverse(annotation: Annot): Annot = {
    annotation.copy(init = initTraverser.traverse(annotation.init))
  }
}
