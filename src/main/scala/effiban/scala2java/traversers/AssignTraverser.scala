package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.Assign

trait AssignTraverser {

  def traverse(assign: Assign, lhsAsComment: Boolean = false): Unit
}

private[traversers] class AssignTraverserImpl(termTraverser: => TermTraverser,
                                              rhsTermTraverser: => RhsTermTraverser)
                                             (implicit javaWriter: JavaWriter) extends AssignTraverser {

  import javaWriter._

  // Variable assignment, named arg in annotation, or named arg in method invocation.
  // Java doesn't support the name in the last case - so in that case the LHS will be written as a comment.
  override def traverse(assign: Assign, lhsAsComment: Boolean = false): Unit = {
    if (lhsAsComment) {
      writeComment(s"${assign.lhs} =")
    } else {
      termTraverser.traverse(assign.lhs)
      write(" = ")
    }
    rhsTermTraverser.traverse(assign.rhs)
  }
}
