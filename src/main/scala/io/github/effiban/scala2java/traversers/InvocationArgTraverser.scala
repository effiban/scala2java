package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.InvocationArgContext
import io.github.effiban.scala2java.entities.Decision.Uncertain
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.{Assign, Block}

trait InvocationArgTraverser {
  def traverse(arg: Term, context: InvocationArgContext = InvocationArgContext()): Unit
}

private[traversers] class InvocationArgTraverserImpl(assignTraverser: => AssignTraverser,
                                                     termFunctionTraverser: => TermFunctionTraverser,
                                                     termTraverser: => TermTraverser)
                                                    (implicit javaWriter: JavaWriter) extends InvocationArgTraverser {

  override def traverse(arg: Term, context: InvocationArgContext = InvocationArgContext()): Unit = arg match {
    case assign: Assign => assignTraverser.traverse(assign = assign, lhsAsComment = context.argNameAsComment)
    // A block cannot be passed as an argument in Java, so wrapping it in a Lambda
    case block: Block => termFunctionTraverser.traverse(Term.Function(Nil, block), shouldBodyReturnValue = Uncertain)
    case term => termTraverser.traverse(term)
  }
}
