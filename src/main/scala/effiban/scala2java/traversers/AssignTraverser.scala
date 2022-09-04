package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.Assign

trait AssignTraverser extends ScalaTreeTraverser[Assign]

private[traversers] class AssignTraverserImpl(termTraverser: => TermTraverser,
                                              rhsTermTraverser: => RhsTermTraverser)
                                             (implicit javaWriter: JavaWriter) extends AssignTraverser {

  import javaWriter._

  // Variable assignment
  override def traverse(assign: Assign): Unit = {
    termTraverser.traverse(assign.lhs)
    write(" = ")
    rhsTermTraverser.traverse(assign.rhs)
  }
}
