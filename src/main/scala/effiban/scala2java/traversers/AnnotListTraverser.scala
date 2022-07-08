package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

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
    annotations.foreach(annotation => {
      annotTraverser.traverse(annotation)
      if (onSameLine) {
        emit(" ")
      } else {
        emitLine()
      }
    })
  }

  override def traverseMods(mods: List[Mod], onSameLine: Boolean = false): Unit = {
    traverseAnnotations(annotations = mods.collect { case annot: Annot => annot },
      onSameLine = onSameLine)
  }

}

object AnnotListTraverser extends AnnotListTraverserImpl(AnnotTraverser)

