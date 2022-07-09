package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.ApplyUnary

trait ApplyUnaryTraverser extends ScalaTreeTraverser[ApplyUnary]

private[traversers] class ApplyUnaryTraverserImpl(termNameTraverser: => TermNameTraverser,
                                                  termTraverser: => TermTraverser)
                                                 (implicit javaWriter: JavaWriter) extends ApplyUnaryTraverser {

  override def traverse(applyUnary: ApplyUnary): Unit = {
    termNameTraverser.traverse(applyUnary.op)
    termTraverser.traverse(applyUnary.arg)
  }
}
