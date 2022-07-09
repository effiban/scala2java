package effiban.scala2java.traversers

import scala.meta.Term

trait PartialFunctionTraverser extends ScalaTreeTraverser[Term.PartialFunction]

private[traversers] class PartialFunctionTraverserImpl(termFunctionTraverser: => TermFunctionTraverser) extends PartialFunctionTraverser {

  override def traverse(partialFunction: Term.PartialFunction): Unit = {
    // The Java equivalent is a full function (lambda) with a switch clause. We need to add a dummy arg for that.
    val dummyArgName = Term.Name("arg")
    val termFunction = Term.Function(
      params = List(Term.Param(mods = Nil, name = dummyArgName, decltpe = None, default = None)),
      body = Term.Match(expr = dummyArgName, cases = partialFunction.cases)
    )
    termFunctionTraverser.traverse(termFunction)
  }
}

object PartialFunctionTraverser extends PartialFunctionTraverserImpl(TermFunctionTraverser)
