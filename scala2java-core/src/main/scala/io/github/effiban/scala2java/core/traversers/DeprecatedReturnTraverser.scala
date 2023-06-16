package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.Return

@deprecated
trait DeprecatedReturnTraverser extends ScalaTreeTraverser[Return]

@deprecated
private[traversers] class DeprecatedReturnTraverserImpl(expressionTermTraverser: => DeprecatedExpressionTermTraverser)
                                                       (implicit javaWriter: JavaWriter) extends DeprecatedReturnTraverser {

  import javaWriter._

  override def traverse(`return`: Return): Unit = {
    write("return ")
    expressionTermTraverser.traverse(`return`.expr)
  }
}
