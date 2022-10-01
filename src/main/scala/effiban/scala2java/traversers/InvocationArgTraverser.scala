package effiban.scala2java.traversers

import effiban.scala2java.contexts.InvocationArgContext
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.Assign

trait InvocationArgTraverser {
  def traverse(arg: Term, context: InvocationArgContext = InvocationArgContext()): Unit
}

private[traversers] class InvocationArgTraverserImpl(assignTraverser: => AssignTraverser,
                                                     termTraverser: => TermTraverser)
                                                    (implicit javaWriter: JavaWriter) extends InvocationArgTraverser {

  override def traverse(arg: Term, context: InvocationArgContext = InvocationArgContext()): Unit = arg match {
    case assign: Assign => assignTraverser.traverse(assign = assign, lhsAsComment = context.argNameAsComment)
    case term => termTraverser.traverse(term)
  }
}
