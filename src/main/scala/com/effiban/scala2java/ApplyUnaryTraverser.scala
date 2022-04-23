package com.effiban.scala2java

import scala.meta.Term.ApplyUnary

object ApplyUnaryTraverser extends ScalaTreeTraverser[ApplyUnary] {

  override def traverse(applyUnary: ApplyUnary): Unit = {
    TermNameTraverser.traverse(applyUnary.op)
    TermTraverser.traverse(applyUnary.arg)
  }
}
