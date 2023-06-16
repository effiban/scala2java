package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.Do

@deprecated
trait DeprecatedDoTraverser extends ScalaTreeTraverser[Do]

@deprecated
private[traversers] class DeprecatedDoTraverserImpl(expressionTermTraverser: => DeprecatedExpressionTermTraverser,
                                                    blockTraverser: => DeprecatedBlockTraverser)
                                                   (implicit javaWriter: JavaWriter) extends DeprecatedDoTraverser {

  import javaWriter._

  override def traverse(`do`: Do): Unit = {
    write("do")
    blockTraverser.traverse(`do`.body)
    write(" while (")
    expressionTermTraverser.traverse(`do`.expr)
    write(")")
  }
}
