package effiban.scala2java.traversers

import scala.meta.Term

trait TermRepeatedTraverser extends ScalaTreeTraverser[Term.Repeated]

private[scala2java] class TermRepeatedTraverserImpl(termTraverser: => TermTraverser) extends TermRepeatedTraverser {

  // Passing vararg param
  override def traverse(termRepeated: Term.Repeated): Unit = {
    //TODO may need to transform to array in Java
    termTraverser.traverse(termRepeated.expr)
  }
}

object TermRepeatedTraverser extends TermRepeatedTraverserImpl(TermTraverser)
