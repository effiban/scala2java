package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Mod.Annot

trait AnnotTraverser extends ScalaTreeTraverser[Annot]

private[traversers] class AnnotTraverserImpl(initTraverser: => InitTraverser)
                                            (implicit javaWriter: JavaWriter) extends AnnotTraverser {
  import javaWriter._

  override def traverse(annotation: Annot): Unit = {
    write("@")
    initTraverser.traverse(annotation.init)
  }
}