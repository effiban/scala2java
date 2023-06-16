package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.Throw

@deprecated
trait DeprecatedThrowTraverser extends ScalaTreeTraverser[Throw]

@deprecated
private[traversers] class DeprecatedThrowTraverserImpl(expressionTermTraverser: => DeprecatedExpressionTermTraverser)
                                                      (implicit javaWriter: JavaWriter) extends DeprecatedThrowTraverser {

  import javaWriter._

  override def traverse(`throw`: Throw): Unit = {
    write("throw ")
    expressionTermTraverser.traverse(`throw`.expr)
  }
}
