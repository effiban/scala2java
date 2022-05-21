package com.effiban.scala2java

import scala.meta.Term
import scala.meta.Term.Eta

trait EtaTraverser extends ScalaTreeTraverser[Eta]

private[scala2java] class EtaTraverserImpl(termTraverser: => TermTraverser) extends EtaTraverser {

  // Eta expansion: Method translated into function, e.g.:  myMethod _
  def traverse(eta: Term.Eta): Unit = {
    //TODO - this will probably not compile in Java, see how it can be improved
    termTraverser.traverse(eta.expr)
  }
}

object EtaTraverser extends EtaTraverserImpl(TermTraverser)
