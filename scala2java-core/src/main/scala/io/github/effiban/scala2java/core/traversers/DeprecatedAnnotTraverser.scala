package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Mod.Annot

@deprecated
trait DeprecatedAnnotTraverser extends ScalaTreeTraverser[Annot]

@deprecated
private[traversers] class DeprecatedAnnotTraverserImpl(initTraverser: => DeprecatedInitTraverser)
                                                      (implicit javaWriter: JavaWriter) extends DeprecatedAnnotTraverser {
  import javaWriter._

  override def traverse(annotation: Annot): Unit = {
    write("@")
    initTraverser.traverse(annotation.init)
  }
}
