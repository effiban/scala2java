package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Mod
import scala.meta.Mod.Annot

@deprecated
trait DeprecatedAnnotListTraverser {
  def traverseAnnotations(annotations: List[Annot], onSameLine: Boolean = false): Unit

  def traverseMods(mods: List[Mod], onSameLine: Boolean = false): Unit
}

@deprecated
private[traversers] class DeprecatedAnnotListTraverserImpl(annotTraverser: => DeprecatedAnnotTraverser)
                                                          (implicit javaWriter: JavaWriter) extends DeprecatedAnnotListTraverser {
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

