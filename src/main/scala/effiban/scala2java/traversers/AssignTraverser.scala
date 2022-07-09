package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.Assign

trait AssignTraverser extends ScalaTreeTraverser[Assign]

private[traversers] class AssignTraverserImpl(termTraverser: => TermTraverser)
                                             (implicit javaWriter: JavaWriter) extends AssignTraverser {

  import javaWriter._

  // Variable assignment
  override def traverse(assign: Assign): Unit = {
    termTraverser.traverse(assign.lhs)
    write(" = ")
    termTraverser.traverse(assign.rhs)
  }
}

object AssignTraverser extends AssignTraverserImpl(TermTraverser)
