package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitArrow}

import scala.meta.Term

object PartialFunctionTraverser extends ScalaTreeTraverser[Term.PartialFunction] {

  override def traverse(partialFunction: Term.PartialFunction): Unit = {
    val dummyArgName = "arg"
    emit(dummyArgName)
    emitArrow()
    GenericTreeTraverser.traverse(Term.Match(expr = Term.Name(dummyArgName), cases = partialFunction.cases))
  }
}
