package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

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
