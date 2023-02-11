package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait AssignLHSTraverser {

  def traverse(lhs: Term, asComment: Boolean = false): Unit
}

private[traversers] class AssignLHSTraverserImpl(termTraverser: => TermTraverser)
                                                (implicit javaWriter: JavaWriter) extends AssignLHSTraverser {

  import javaWriter._

  // The LHS of a `var` assignment, named arg in annotation, or named arg in method invocation.
  // Java doesn't support the name in the last case - so in that case it will be written as a comment.
  override def traverse(lhs: Term, asComment: Boolean = false): Unit = {
    if (asComment) {
      writeComment(s"$lhs =")
    } else {
      termTraverser.traverse(lhs)
      write(" = ")
    }
  }
}
