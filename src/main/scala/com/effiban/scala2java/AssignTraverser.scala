package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.Assign

object AssignTraverser extends ScalaTreeTraverser[Assign] {

  // Variable assignment
  override def traverse(assign: Assign): Unit = {
    TermTraverser.traverse(assign.lhs)
    emit(" = ")
    TermTraverser.traverse(assign.rhs)
  }
}
