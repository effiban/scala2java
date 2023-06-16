package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.While

@deprecated
trait DeprecatedWhileTraverser extends ScalaTreeTraverser[While]

@deprecated
private[traversers] class DeprecatedWhileTraverserImpl(expressionTermTraverser: => DeprecatedExpressionTermTraverser,
                                                       blockTraverser: => DeprecatedBlockTraverser)
                                                      (implicit javaWriter: JavaWriter) extends DeprecatedWhileTraverser {

  import javaWriter._

  override def traverse(`while`: While): Unit = {
    write("while (")
    expressionTermTraverser.traverse(`while`.expr)
    write(")")
    blockTraverser.traverse(`while`.body)
  }
}
