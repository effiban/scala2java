package com.effiban.scala2java

import scala.meta.Term.ApplyUnary

object ApplyUnaryTraverser extends ScalaTreeTraverser[ApplyUnary] {

  override def traverse(applyUnary: ApplyUnary): Unit = {
    GenericTreeTraverser.traverse(applyUnary.op)
    GenericTreeTraverser.traverse(applyUnary.arg)
  }
}
