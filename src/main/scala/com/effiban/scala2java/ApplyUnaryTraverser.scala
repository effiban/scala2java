package com.effiban.scala2java

import scala.meta.Term.ApplyUnary

trait ApplyUnaryTraverser extends ScalaTreeTraverser[ApplyUnary]

object ApplyUnaryTraverser extends ApplyUnaryTraverser {

  override def traverse(applyUnary: ApplyUnary): Unit = {
    TermNameTraverser.traverse(applyUnary.op)
    TermTraverser.traverse(applyUnary.arg)
  }
}
