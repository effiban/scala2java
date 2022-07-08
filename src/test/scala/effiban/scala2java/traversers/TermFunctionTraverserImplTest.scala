package effiban.scala2java.traversers

import effiban.scala2java.UnitTestSuite
import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import org.mockito.ArgumentMatchers

import scala.meta.{Name, Term}

class TermFunctionTraverserImplTest extends UnitTestSuite {
  private val termParamTraverser = mock[TermParamTraverser]
  private val termParamListTraverser = mock[TermParamListTraverser]
  private val termTraverser = mock[TermTraverser]

  private val termFunctionTraverser = new TermFunctionTraverserImpl(
    termParamTraverser,
    termParamListTraverser,
    termTraverser
  )

  test("traverse with zero args") {
    val functionBody = Term.Apply(Term.Name("doSomething"), Nil)

    doWrite("()").when(termParamListTraverser).traverse(
      termParams = ArgumentMatchers.eq(Nil),
      onSameLine = ArgumentMatchers.eq(true)
    )
    doWrite("doSomething();").when(termTraverser).traverse(eqTree(functionBody))

    termFunctionTraverser.traverse(Term.Function(params = Nil, body = functionBody))

    outputWriter.toString shouldBe "() -> doSomething();"
  }

  test("traverse with one arg") {
    val param = termParam("val1")
    val functionBody = Term.Apply(Term.Name("doSomething"), List(Term.Name("val1")))
    val function = Term.Function(params = List(param), body = functionBody)

    doWrite("val1").when(termParamTraverser).traverse(eqTree(param))
    doWrite("doSomething(val1);").when(termTraverser).traverse(eqTree(functionBody))

    termFunctionTraverser.traverse(function)

    outputWriter.toString shouldBe "val1 -> doSomething(val1);"
  }

  test("traverse with two args") {
    val params = List(termParam("val1"), termParam("val2"))
    val functionBody = Term.Apply(Term.Name("doSomething"), List(Term.Name("val1"), Term.Name("val2")))
    val function = Term.Function(params = params, body = functionBody)

    doWrite("(val1, val2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(params),
      onSameLine = ArgumentMatchers.eq(true)
    )
    doWrite("doSomething(val1, val2);").when(termTraverser).traverse(eqTree(functionBody))

    termFunctionTraverser.traverse(function)

    outputWriter.toString shouldBe "(val1, val2) -> doSomething(val1, val2);"
  }

  private def termParam(name: String) = {
    Term.Param(mods = Nil, name = Name.Indeterminate(name), decltpe = None, default = None)
  }
}
