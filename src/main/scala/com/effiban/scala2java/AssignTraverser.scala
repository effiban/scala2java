package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.Assign

object AssignTraverser extends ScalaTreeTraverser[Assign] {

  // Variable assignment
  override def traverse(assign: Assign): Unit = {
    GenericTreeTraverser.traverse(assign.lhs)
    emit(" = ")
    GenericTreeTraverser.traverse(assign.rhs)
  }
}
