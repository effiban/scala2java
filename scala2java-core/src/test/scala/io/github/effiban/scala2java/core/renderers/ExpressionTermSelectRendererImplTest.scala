package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ExpressionTermSelectRendererImplTest extends UnitTestSuite {
  private val MyObject = q"MyObject"
  private val MyMethod = q"myMethod"
  private val MyMethod2 = q"myMethod2"
  private val SelectWithTermName = Term.Select(qual = MyObject, name = MyMethod)

  private val expressionTermRenderer = mock[ExpressionTermRenderer]
  private val typeListRenderer = mock[TypeListRenderer]
  private val termNameRenderer = mock[TermNameRenderer]

  private val termSelectRenderer = new ExpressionTermSelectRendererImpl(
    expressionTermRenderer,
    typeListRenderer,
    termNameRenderer,
  )

  test("render() when qualifier is a Term.Name and has type args") {
    val typeArg = TypeNames.Int
    val typeArgs = List(typeArg)
    val context = TermSelectContext(appliedTypeArgs = typeArgs)

    doWrite("MyObject").when(expressionTermRenderer).render(eqTree(MyObject))
    doWrite("<Integer>").when(typeListRenderer).render(eqTreeList(typeArgs))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MyMethod))

    termSelectRenderer.render(SelectWithTermName, context)

    outputWriter.toString shouldBe "MyObject.<Integer>myMethod"
  }
  
  test("render() when qualifier is a Term.Name and has no type args") {

    doWrite("MyObject").when(expressionTermRenderer).render(eqTree(MyObject))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MyMethod))
    termSelectRenderer.render(SelectWithTermName)

    outputWriter.toString shouldBe "MyObject.myMethod"
  }

  test("render() when qualifier is a Term.Function should wrap in parentheses") {
    val termFunction = Term.Function(Nil, Lit.Int(1))
    val termSelect = Term.Select(termFunction, MyMethod)

    doWrite("() -> 1").when(expressionTermRenderer).render(eqTree(termFunction))
    doWrite("get").when(termNameRenderer).render(eqTree(MyMethod))
    termSelectRenderer.render(termSelect)

    outputWriter.toString shouldBe "(() -> 1).get"
  }

  test("render() when qualifier is a Term.Ascribe applied to a Term.Function, should wrap in parentheses") {
    val termFunction = Term.Function(Nil, Lit.Int(1))
    val ascribedTermFunction = Term.Ascribe(termFunction, t"Supplier[Int]")
    val termSelect = Term.Select(ascribedTermFunction, MyMethod)

    doWrite("(Supplier<Integer>)() -> 1").when(expressionTermRenderer).render(eqTree(ascribedTermFunction))
    doWrite("get").when(termNameRenderer).render(eqTree(MyMethod))

    termSelectRenderer.render(termSelect)

    outputWriter.toString shouldBe "((Supplier<Integer>)() -> 1).get"
  }

  test("render() when qualifier is a Term.Apply should break the line") {
    val arg = List(Term.Name("arg1"))

    val qual = Term.Apply(SelectWithTermName, arg)
    val select = Term.Select(qual, MyMethod2)

    doWrite("MyObject.myMethod(arg1)").when(expressionTermRenderer).render(eqTree(qual))
    doWrite("myMethod2").when(termNameRenderer).render(eqTree(MyMethod2))
    termSelectRenderer.render(select)

    outputWriter.toString shouldBe
      """MyObject.myMethod(arg1)
        |.myMethod2""".stripMargin
  }
}
