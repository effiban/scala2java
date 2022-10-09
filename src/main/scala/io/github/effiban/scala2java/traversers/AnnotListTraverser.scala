package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Mod
import scala.meta.Mod.Annot

trait AnnotListTraverser {
  def traverseAnnotations(annotations: List[Annot], onSameLine: Boolean = false): Unit

  def traverseMods(mods: List[Mod], onSameLine: Boolean = false): Unit
}

private[traversers] class AnnotListTraverserImpl(annotTraverser: => AnnotTraverser)
                                                (implicit javaWriter: JavaWriter) extends AnnotListTraverser {
  import javaWriter._

  override def traverseAnnotations(annotations: List[Annot], onSameLine: Boolean = false): Unit = {
    annotations.foreach(annotation => {
      annotTraverser.traverse(annotation)
      if (onSameLine) {
        write(" ")
      } else {
        writeLine()
      }
    })
  }

  override def traverseMods(mods: List[Mod], onSameLine: Boolean = false): Unit = {
    traverseAnnotations(annotations = mods.collect { case annot: Annot => annot },
      onSameLine = onSameLine)
  }

}

