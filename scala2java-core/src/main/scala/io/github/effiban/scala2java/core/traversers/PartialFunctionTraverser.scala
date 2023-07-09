package io.github.effiban.scala2java.core.traversers

import scala.meta.{Term, XtensionQuasiquoteTerm}

trait PartialFunctionTraverser {
  def traverse(partialFunction: Term.PartialFunction): Term.Function
}

private[traversers] class PartialFunctionTraverserImpl(termFunctionTraverser: => TermFunctionTraverser) extends PartialFunctionTraverser {

  override def traverse(partialFunction: Term.PartialFunction): Term.Function = {
    // The Java equivalent is a full function (lambda) with a switch clause. We need to add a dummy arg for that.
    val dummyArgName = q"arg"
    val termFunction = Term.Function(
      params = List(Term.Param(mods = Nil, name = dummyArgName, decltpe = None, default = None)),
      // TODO - add the default case which does nothing to make the match exhaustive
      body = Term.Match(expr = dummyArgName, cases = partialFunction.cases)
    )
    termFunctionTraverser.traverse(termFunction)
  }
}
