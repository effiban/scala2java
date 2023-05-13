package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.renderers.TypeRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.Ascribe

trait AscribeTraverser extends ScalaTreeTraverser[Ascribe]

private[traversers] class AscribeTraverserImpl(typeTraverser: => TypeTraverser,
                                               typeRenderer: => TypeRenderer,
                                               expressionTermTraverser: => ExpressionTermTraverser)
                                              (implicit javaWriter: JavaWriter) extends AscribeTraverser {

  import javaWriter._

  // Explicitly specified type, e.g.: 2:Short
  // Java equivalent is casting. e.g. (short)2
  override def traverse(ascribe: Ascribe): Unit = {
    writeStartDelimiter(Parentheses)
    val traversedType = typeTraverser.traverse(ascribe.tpe)
    typeRenderer.render(traversedType)
    writeEndDelimiter(Parentheses)
    expressionTermTraverser.traverse(ascribe.expr)
  }
}
