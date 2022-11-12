package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.Throw

trait ThrowTraverser extends ScalaTreeTraverser[Throw]

private[traversers] class ThrowTraverserImpl(termTraverser: => TermTraverser)
                                            (implicit javaWriter: JavaWriter) extends ThrowTraverser {

  import javaWriter._

  override def traverse(`throw`: Throw): Unit = {
    write("throw ")
    termTraverser.traverse(`throw`.expr)
  }
}