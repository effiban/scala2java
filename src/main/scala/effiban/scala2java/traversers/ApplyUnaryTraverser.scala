package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.Term.ApplyUnary

trait ApplyUnaryTraverser extends ScalaTreeTraverser[ApplyUnary]

private[scala2java] class ApplyUnaryTraverserImpl(termNameTraverser: => TermNameTraverser,
                                                  termTraverser: => TermTraverser)
                                                 (implicit javaEmitter: JavaEmitter) extends ApplyUnaryTraverser {

  override def traverse(applyUnary: ApplyUnary): Unit = {
    termNameTraverser.traverse(applyUnary.op)
    termTraverser.traverse(applyUnary.arg)
  }
}

object ApplyUnaryTraverser extends ApplyUnaryTraverserImpl(TermNameTraverser, TermTraverser)
