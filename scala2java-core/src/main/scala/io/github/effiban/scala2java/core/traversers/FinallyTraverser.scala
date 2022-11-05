package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait FinallyTraverser extends ScalaTreeTraverser[Term]

private[traversers] class FinallyTraverserImpl(blockTraverser: => BlockTraverser)
                                              (implicit javaWriter: JavaWriter) extends FinallyTraverser {

  import javaWriter._

  // TODO support return value flag
  override def traverse(finallyp: Term): Unit = {
    write("finally")
    blockTraverser.traverse(finallyp)
  }
}
