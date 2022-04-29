package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.Assign

trait AssignTraverser extends ScalaTreeTraverser[Assign]

object AssignTraverser extends AssignTraverser {

  // Variable assignment
  override def traverse(assign: Assign): Unit = {
    TermTraverser.traverse(assign.lhs)
    emit(" = ")
    TermTraverser.traverse(assign.rhs)
  }
}
