package effiban.scala2java

import scala.meta.Term.Assign

trait AssignTraverser extends ScalaTreeTraverser[Assign]

private[scala2java] class AssignTraverserImpl(termTraverser: => TermTraverser)
                                             (implicit javaEmitter: JavaEmitter) extends AssignTraverser {

  import javaEmitter._

  // Variable assignment
  override def traverse(assign: Assign): Unit = {
    termTraverser.traverse(assign.lhs)
    emit(" = ")
    termTraverser.traverse(assign.rhs)
  }
}

object AssignTraverser extends AssignTraverserImpl(TermTraverser)
