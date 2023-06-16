package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.Decision.{Decision, Uncertain}

import scala.meta.Term

@deprecated
trait DeprecatedPartialFunctionTraverser {
  def traverse(partialFunction: Term.PartialFunction, shouldBodyReturnValue: Decision = Uncertain): Unit
}

@deprecated
private[traversers] class DeprecatedPartialFunctionTraverserImpl(termFunctionTraverser: => DeprecatedTermFunctionTraverser) extends DeprecatedPartialFunctionTraverser {

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
