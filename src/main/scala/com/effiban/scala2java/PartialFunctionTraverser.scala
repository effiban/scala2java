package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitArrow}

import scala.meta.Term

trait PartialFunctionTraverser extends ScalaTreeTraverser[Term.PartialFunction]

private[scala2java] class PartialFunctionTraverserImpl(termMatchTraverser: => TermMatchTraverser)
                                                      (implicit javaEmitter: JavaEmitter) extends PartialFunctionTraverser {

  override def traverse(partialFunction: Term.PartialFunction): Unit = {
    val dummyArgName = "arg"
    emit(dummyArgName)
    emitArrow()
    termMatchTraverser.traverse(Term.Match(expr = Term.Name(dummyArgName), cases = partialFunction.cases))
  }
}

object PartialFunctionTraverser extends PartialFunctionTraverserImpl(TermMatchTraverser)
