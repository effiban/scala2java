package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.Do

trait DoTraverser extends ScalaTreeTraverser[Do]

private[traversers] class DoTraverserImpl(termTraverser: => TermTraverser,
                                          blockTraverser: => BlockTraverser)
                                         (implicit javaWriter: JavaWriter) extends DoTraverser {

  import javaWriter._

  override def traverse(`do`: Do): Unit = {
    write("do")
    blockTraverser.traverse(`do`.body)
    write(" while (")
    termTraverser.traverse(`do`.expr)
    write(")")
  }
}
