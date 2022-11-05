package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.While

trait WhileTraverser extends ScalaTreeTraverser[While]

private[traversers] class WhileTraverserImpl(termTraverser: => TermTraverser,
                                             blockTraverser: => BlockTraverser)
                                            (implicit javaWriter: JavaWriter) extends WhileTraverser {

  import javaWriter._

  override def traverse(`while`: While): Unit = {
    write("while (")
    termTraverser.traverse(`while`.expr)
    write(")")
    blockTraverser.traverse(`while`.body)
  }
}
