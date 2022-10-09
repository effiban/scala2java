package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.entities.Decision.{Decision, Uncertain}

import scala.meta.Term

trait PartialFunctionTraverser {
  def traverse(partialFunction: Term.PartialFunction, shouldBodyReturnValue: Decision = Uncertain): Unit
}

private[traversers] class PartialFunctionTraverserImpl(termFunctionTraverser: => TermFunctionTraverser) extends PartialFunctionTraverser {

  override def traverse(partialFunction: Term.PartialFunction, shouldBodyReturnValue: Decision = Uncertain): Unit = {
    // The Java equivalent is a full function (lambda) with a switch clause. We need to add a dummy arg for that.
    val dummyArgName = Term.Name("arg")
    val termFunction = Term.Function(
      params = List(Term.Param(mods = Nil, name = dummyArgName, decltpe = None, default = None)),
      body = Term.Match(expr = dummyArgName, cases = partialFunction.cases)
    )
    termFunctionTraverser.traverse(termFunction, shouldBodyReturnValue)
  }
}
