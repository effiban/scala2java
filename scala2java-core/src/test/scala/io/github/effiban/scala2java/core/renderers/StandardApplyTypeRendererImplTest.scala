package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.matchers.TermSelectContextMatcher.eqTermSelectContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class StandardApplyTypeRendererImplTest extends UnitTestSuite {

  private val expressionTermSelectRenderer = mock[ExpressionTermSelectRenderer]
  private val typeListRenderer = mock[TypeListRenderer]
  private val expressionTermRenderer = mock[ExpressionTermRenderer]
  private val termApplyRenderer = mock[TermApplyRenderer]

  private val standardApplyTypeRenderer = new StandardApplyTypeRendererImpl(
    expressionTermSelectRenderer,
    typeListRenderer,
    expressionTermRenderer
  )

  test("render() when function is a 'Select', should render properly") {
    val fun = Term.Select(Term.Name("myObj"), Term.Name("myFunc1"))
    val typeArgs = List(Type.Name("T1"), Type.Name("T2"))

    doWrite("myObj<T1, T2>.myFunc")
      .when(expressionTermSelectRenderer).render(eqTree(fun), eqTermSelectContext(TermSelectContext(typeArgs)))

    standardApplyTypeRenderer.render(Term.ApplyType(fun = fun, targs = typeArgs))

    outputWriter.toString shouldBe "myObj<T1, T2>.myFunc"

    verifyNoMoreInteractions(expressionTermRenderer, termApplyRenderer)
  }

  test("render() when function is a 'Term.Name', should prefix with a commented 'this'") {
    val fun = q"myFunc1"

    val typeArg1 = t"T1"
    val typeArg2 = t"T2"
    val typeArgs = List(typeArg1, typeArg2)

    doWrite("myFunc").when(expressionTermRenderer).render(eqTree(fun))
    doWrite("<T1, T2>").when(typeListRenderer).render(eqTreeList(typeArgs))

    standardApplyTypeRenderer.render(Term.ApplyType(fun = fun, targs = typeArgs))

    outputWriter.toString shouldBe "/* this? */.<T1, T2>myFunc"

    verifyNoMoreInteractions(expressionTermSelectRenderer, termApplyRenderer)
  }
}
