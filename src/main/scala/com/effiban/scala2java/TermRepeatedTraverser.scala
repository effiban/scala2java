package com.effiban.scala2java

import scala.meta.Term

object TermRepeatedTraverser {

  // Passing vararg param
  def traverse(termRepeated: Term.Repeated): Unit = {
    // TODO may need to transform to array in Java
    TermTraverser.traverse(termRepeated.expr)
  }
}
