package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitArrow}

import scala.meta.Term

trait PartialFunctionTraverser extends ScalaTreeTraverser[Term.PartialFunction]

object PartialFunctionTraverser extends PartialFunctionTraverser {

  override def traverse(partialFunction: Term.PartialFunction): Unit = {
    val dummyArgName = "arg"
    emit(dummyArgName)
    emitArrow()
    TermMatchTraverser.traverse(Term.Match(expr = Term.Name(dummyArgName), cases = partialFunction.cases))
  }
}
