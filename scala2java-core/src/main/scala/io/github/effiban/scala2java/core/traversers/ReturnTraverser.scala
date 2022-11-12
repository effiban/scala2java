package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.Return

trait ReturnTraverser extends ScalaTreeTraverser[Return]

private[traversers] class ReturnTraverserImpl(termTraverser: => TermTraverser)
                                             (implicit javaWriter: JavaWriter) extends ReturnTraverser {

  import javaWriter._

  override def traverse(`return`: Return): Unit = {
    write("return ")
    termTraverser.traverse(`return`.expr)
  }
}