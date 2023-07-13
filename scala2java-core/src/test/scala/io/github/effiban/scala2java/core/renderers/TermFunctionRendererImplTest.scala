package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Lit, Name, Term, XtensionQuasiquoteTermParam}

class TermFunctionRendererImplTest extends UnitTestSuite {

  private val termParamRenderer = mock[TermParamRenderer]
  private val termParamListRenderer = mock[TermParamListRenderer]
  private val blockRenderer = mock[BlockRenderer]
  private val defaultTermRenderer = mock[DefaultTermRenderer]

  private val termFunctionRenderer = new TermFunctionRendererImpl(
    termParamRenderer,
    termParamListRenderer,
    blockRenderer,
    defaultTermRenderer
  )


  test("render with zero args and one term") {
    val functionBody = Term.Apply(Term.Name("doSomething"), Nil)

    doWrite("()").when(termParamListRenderer).render(
      termParams = eqTo(Nil),
      context = eqTo(TermParamListRenderContext(onSameLine = true))
    )
    doWrite("doSomething()").when(defaultTermRenderer).render(eqTree(functionBody))

    termFunctionRenderer.render(Term.Function(params = Nil, body = functionBody))

    outputWriter.toString shouldBe "() -> doSomething()"
  }

  test("render with one untyped arg and one term") {
    val param = termParam("val1")
    val functionBody = Term.Apply(Term.Name("doSomething"), List(Term.Name("val1")))
    val function = Term.Function(params = List(param), body = functionBody)

    doWrite("val1").when(termParamRenderer).render(termParam = eqTree(param))
    doWrite("doSomething(val1)").when(defaultTermRenderer).render(eqTree(functionBody))

    termFunctionRenderer.render(function)

    outputWriter.toString shouldBe "val1 -> doSomething(val1)"
  }

  test("render with one typed arg and one term") {
    val param = param"val1: Int"
    val functionBody = Term.Apply(Term.Name("doSomething"), List(Term.Name("val1")))
    val function = Term.Function(params = List(param), body = functionBody)

    doWrite("(int val1)").when(termParamListRenderer).render(
      termParams = eqTreeList(List(param)),
      context = eqTo(TermParamListRenderContext(onSameLine = true))
    )
    doWrite("doSomething(val1)")
      .when(defaultTermRenderer).render(eqTree(functionBody))

    termFunctionRenderer.render(function)

    outputWriter.toString shouldBe "(int val1) -> doSomething(val1)"
  }

  test("render with one arg and block when uncertainReturn=false") {
    val param = termParam("val1")
    val functionBody = Term.Block(
      List(
        Term.Apply(Term.Name("doSomething"), List(Term.Name("val1"))),
        Lit.Int(3)
      )
    )
    val function = Term.Function(params = List(param), body = functionBody)

    doWrite("val1").when(termParamRenderer).render(termParam = eqTree(param))
    doWrite(
      """ {
        |  /* BODY */
        |}""".stripMargin
    ).when(blockRenderer).render(
      block = eqTree(functionBody), context = eqTo(BlockRenderContext())
    )

    termFunctionRenderer.render(function)

    outputWriter.toString shouldBe
      """val1 ->  {
        |  /* BODY */
        |}""".stripMargin
  }

  test("render with one arg and block when uncertainReturn=true") {
    val param = termParam("val1")
    val functionBody = Term.Block(
      List(
        Term.Apply(Term.Name("doSomething"), List(Term.Name("val1"))),
        Lit.Int(3)
      )
    )
    val function = Term.Function(params = List(param), body = functionBody)

    val bodyContext = BlockRenderContext(uncertainReturn = true)

    doWrite("val1").when(termParamRenderer).render(termParam = eqTree(param))
    doWrite(
      """ {
        |  /* BODY */
        |  /* return? */ last;
        |}""".stripMargin
    ).when(blockRenderer).render(
      block = eqTree(functionBody), context = eqTo(bodyContext)
    )

    termFunctionRenderer.render(function, context = TermFunctionRenderContext(uncertainReturn = true))

    outputWriter.toString shouldBe
      """val1 ->  {
        |  /* BODY */
        |  /* return? */ last;
        |}""".stripMargin
  }

  test("render with two args and one term") {
    val params = List(termParam("val1"), termParam("val2"))
    val functionBody = Term.Apply(Term.Name("doSomething"), List(Term.Name("val1"), Term.Name("val2")))
    val function = Term.Function(params = params, body = functionBody)

    doWrite("(val1, val2)").when(termParamListRenderer).render(
      termParams = eqTreeList(params),
      context = eqTo(TermParamListRenderContext(onSameLine = true))
    )
    doWrite("doSomething(val1, val2)")
      .when(defaultTermRenderer).render(eqTree(functionBody))

    termFunctionRenderer.render(function)

    outputWriter.toString shouldBe "(val1, val2) -> doSomething(val1, val2)"
  }


  private def termParam(name: String) = {
    Term.Param(mods = Nil, name = Name.Indeterminate(name), decltpe = None, default = None)
  }
}
