package com.effiban.scala2java

import scala.meta.Term.Do

trait DoTraverser extends ScalaTreeTraverser[Do]

private[scala2java] class DoTraverserImpl(termTraverser: => TermTraverser)
                                         (implicit javaEmitter: JavaEmitter) extends DoTraverser {

  import javaEmitter._

  override def traverse(`do`: Do): Unit = {
    emit("do ")
    termTraverser.traverse(`do`.body)
    emit("while (")
    termTraverser.traverse(`do`.expr)
    emit(")")
  }
}

object DoTraverser extends DoTraverserImpl(TermTraverser)
