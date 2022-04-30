package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.Assign

trait AssignTraverser extends ScalaTreeTraverser[Assign]

private[scala2java] class AssignTraverserImpl(termTraverser: => TermTraverser) extends AssignTraverser {

  // Variable assignment
  override def traverse(assign: Assign): Unit = {
    termTraverser.traverse(assign.lhs)
    emit(" = ")
    termTraverser.traverse(assign.rhs)
  }
}

object AssignTraverser extends AssignTraverserImpl(TermTraverser)
