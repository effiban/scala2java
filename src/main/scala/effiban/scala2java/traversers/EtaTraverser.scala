package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.Term
import scala.meta.Term.Eta

trait EtaTraverser extends ScalaTreeTraverser[Eta]

private[scala2java] class EtaTraverserImpl(termTraverser: => TermTraverser)
                                          (implicit javaEmitter: JavaEmitter) extends EtaTraverser {

  import javaEmitter._

  // Eta expansion: Method translated into function, e.g.:  myMethod _
  def traverse(eta: Term.Eta): Unit = {
    // Java equivalent is a method reference, but it will only work if assigned to a typed variable on the LHS.
    // We also can't tell if it is an instance method or class method, so arbitrarily using `this`
    //TODO if type inference is added later on, use that to ensure correctness
    emit("this::")
    termTraverser.traverse(eta.expr)
  }
}

object EtaTraverser extends EtaTraverserImpl(TermTraverser)
