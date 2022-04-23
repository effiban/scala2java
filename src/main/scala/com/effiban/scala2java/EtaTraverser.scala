package com.effiban.scala2java

import scala.meta.Term
import scala.meta.Term.Eta

object EtaTraverser extends ScalaTreeTraverser[Eta] {

  // Eta expansion: Method translated into function, e.g.:  myMethod _
  def traverse(eta: Term.Eta): Unit = {
    // TODO - this will probably not compile in Java, see how it can be improved
    GenericTreeTraverser.traverse(eta.expr)
  }
}
