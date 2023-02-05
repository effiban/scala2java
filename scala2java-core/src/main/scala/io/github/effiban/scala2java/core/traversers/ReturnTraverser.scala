package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.Return

trait ReturnTraverser extends ScalaTreeTraverser[Return]

private[traversers] class ReturnTraverserImpl(expressionTraverser: => ExpressionTraverser)
                                             (implicit javaWriter: JavaWriter) extends ReturnTraverser {

  import javaWriter._

  override def traverse(`return`: Return): Unit = {
    write("return ")
    expressionTraverser.traverse(`return`.expr)
  }
}
