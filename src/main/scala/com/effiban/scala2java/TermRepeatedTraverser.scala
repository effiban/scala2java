package com.effiban.scala2java

import scala.meta.Term

trait TermRepeatedTraverser extends ScalaTreeTraverser[Term.Repeated]

object TermRepeatedTraverser extends TermRepeatedTraverser {

  // Passing vararg param
  override def traverse(termRepeated: Term.Repeated): Unit = {
    // TODO may need to transform to array in Java
    TermTraverser.traverse(termRepeated.expr)
  }
}
