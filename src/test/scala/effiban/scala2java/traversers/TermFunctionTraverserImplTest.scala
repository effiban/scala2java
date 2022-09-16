package effiban.scala2java.traversers

import effiban.scala2java.contexts.StatContext
import effiban.scala2java.entities.Decision.{Uncertain, Yes}
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.Term.Block
import scala.meta.{Lit, Name, Term}

class TermFunctionTraverserImplTest extends UnitTestSuite {
  private val termParamTraverser = mock[TermParamTraverser]
  private val termParamListTraverser = mock[TermParamListTraverser]
  private val statTraverser = mock[StatTraverser]
  private val blockTraverser = mock[BlockTraverser]

  private val termFunctionTraverser = new TermFunctionTraverserImpl(
    termParamTraverser,
    termParamListTraverser,
    statTraverser,
    blockTraverser
  )


  test("traverse with zero args and one statement") {
    val functionBody = Term.Apply(Term.Name("doSomething"), Nil)

    doWrite("()").when(termParamListTraverser).traverse(
      termParams = ArgumentMatchers.eq(Nil),
      onSameLine = ArgumentMatchers.eq(true)
    )
    doWrite("doSomething();")
      .when(statTraverser).traverse(eqTree(functionBody), ArgumentMatchers.eq(StatContext()))

    termFunctionTraverser.traverse(Term.Function(params = Nil, body = functionBody))

    outputWriter.toString shouldBe "() -> doSomething();"
  }

  test("traverse with one arg and one statement") {
    val param = termParam("val1")
    val functionBody = Term.Apply(Term.Name("doSomething"), List(Term.Name("val1")))
    val function = Term.Function(params = List(param), body = functionBody)

    doWrite("val1").when(termParamTraverser).traverse(eqTree(param))
    doWrite("doSomething(val1);")
      .when(statTraverser).traverse(eqTree(functionBody), ArgumentMatchers.eq(StatContext()))

    termFunctionTraverser.traverse(function)

    outputWriter.toString shouldBe "val1 -> doSomething(val1);"
  }

  test("traverse with one arg and block of one statement") {
    val param = termParam("val1")
    val bodyTerm = Term.Apply(Term.Name("doSomething"), List(Term.Name("val1")))
    val function = Term.Function(params = List(param), body = Block(List(bodyTerm)))

    doWrite("val1").when(termParamTraverser).traverse(eqTree(param))
    doWrite("doSomething(val1);")
      .when(statTraverser).traverse(eqTree(bodyTerm), ArgumentMatchers.eq(StatContext()))

    termFunctionTraverser.traverse(function)

    outputWriter.toString shouldBe "val1 -> doSomething(val1);"
  }

  test("traverse with two args and one statement") {
    val params = List(termParam("val1"), termParam("val2"))
    val functionBody = Term.Apply(Term.Name("doSomething"), List(Term.Name("val1"), Term.Name("val2")))
    val function = Term.Function(params = params, body = functionBody)

    doWrite("(val1, val2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(params),
      onSameLine = ArgumentMatchers.eq(true)
    )
    doWrite("doSomething(val1, val2);")
      .when(statTraverser).traverse(eqTree(functionBody), ArgumentMatchers.eq(StatContext()))

    termFunctionTraverser.traverse(function)

    outputWriter.toString shouldBe "(val1, val2) -> doSomething(val1, val2);"
  }

  test("traverse with one arg and two statements") {
    val param = termParam("val1")
    val functionBody = Term.Block(
      List(
        Term.Apply(Term.Name("doSomething"), List(Term.Name("val1"))),
        Lit.Int(3)
      )
    )
    val function = Term.Function(params = List(param), body = functionBody)

    doWrite("val1").when(termParamTraverser).traverse(eqTree(param))
    doWrite(
      """ {
        |  /* BODY */
        |}""".stripMargin
    ).when(blockTraverser).traverse(
      stat = eqTree(functionBody),
      shouldReturnValue = ArgumentMatchers.eq(Uncertain),
      maybeInit = ArgumentMatchers.eq(None)
    )

    termFunctionTraverser.traverse(function)

    outputWriter.toString shouldBe
      """val1 ->  {
        |  /* BODY */
        |}""".stripMargin
  }

  test("traverse with one arg and two statements when shouldBodyReturnValue='Yes'") {
    val param = termParam("val1")
    val functionBody = Term.Block(
      List(
        Term.Apply(Term.Name("doSomething"), List(Term.Name("val1"))),
        Lit.Int(3)
      )
    )
    val function = Term.Function(params = List(param), body = functionBody)

    doWrite("val1").when(termParamTraverser).traverse(eqTree(param))
    doWrite(
      """ {
        |  /* BODY */
        |}""".stripMargin
    ).when(blockTraverser).traverse(
      stat = eqTree(functionBody),
      shouldReturnValue = ArgumentMatchers.eq(Yes),
      maybeInit = ArgumentMatchers.eq(None)
    )

    termFunctionTraverser.traverse(function, shouldBodyReturnValue = Yes)

    outputWriter.toString shouldBe
      """val1 ->  {
        |  /* BODY */
        |}""".stripMargin
  }

  private def termParam(name: String) = {
    Term.Param(mods = Nil, name = Name.Indeterminate(name), decltpe = None, default = None)
  }
}
