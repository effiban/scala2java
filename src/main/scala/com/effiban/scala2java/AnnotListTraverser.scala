package com.effiban.scala2java

import scala.meta.Mod
import scala.meta.Mod.Annot

trait AnnotListTraverser {
  def traverseAnnotations(annotations: List[Annot], onSameLine: Boolean = false): Unit

  def traverseMods(mods: List[Mod], onSameLine: Boolean = false): Unit
}

private[scala2java] class AnnotListTraverserImpl(annotTraverser: => AnnotTraverser)
                                                (implicit javaEmitter: JavaEmitter) extends AnnotListTraverser {

  import javaEmitter._

  override def traverseAnnotations(annotations: List[Annot], onSameLine: Boolean = false): Unit = {
    annotations.zipWithIndex.foreach { case (annotation, idx) =>
      annotTraverser.traverse(annotation)
      onSameLine match {
        case true if idx < annotations.size - 1 => emit(" ")
        case true =>
        case false => emitLine()
      }
    }
  }

  override def traverseMods(mods: List[Mod], onSameLine: Boolean = false): Unit = {
    traverseAnnotations(annotations = mods.collect { case annot: Annot => annot },
      onSameLine = onSameLine)
  }

}

object AnnotListTraverser extends AnnotListTraverserImpl(AnnotTraverser)

