package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

@deprecated
trait DeprecatedFinallyTraverser extends ScalaTreeTraverser[Term]

@deprecated
private[traversers] class DeprecatedFinallyTraverserImpl(blockTraverser: => DeprecatedBlockTraverser)
                                                        (implicit javaWriter: JavaWriter) extends DeprecatedFinallyTraverser {

  import javaWriter._

  // TODO support return value flag
  override def traverse(finallyp: Term): Unit = {
    write("finally")
    blockTraverser.traverse(finallyp)
  }
}
