package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait AssignLHSRenderer {

  def render(lhs: Term, asComment: Boolean = false): Unit
}

private[renderers] class AssignLHSRendererImpl(expressionTermRenderer: => ExpressionTermRenderer)
                                              (implicit javaWriter: JavaWriter) extends AssignLHSRenderer {

  import javaWriter._

  // The LHS of a `var` assignment, named arg in annotation, or named arg in method invocation.
  // Java doesn't support the name in the last case - so in that case it will be written as a comment.
  override def render(lhs: Term, asComment: Boolean = false): Unit = {
    if (asComment) {
      writeComment(s"$lhs =")
    } else {
      expressionTermRenderer.render(lhs)
      write(" = ")
    }
  }
}
