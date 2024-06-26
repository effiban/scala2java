package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.CurlyBrace
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.renderers.contexts.{ArrayInitializerSizeRenderContext, ArrayInitializerValuesRenderContext}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ArrayInitializerRendererImplTest extends UnitTestSuite {
  private val ExpectedListTraversalOptions = ListTraversalOptions(maybeEnclosingDelimiter = Some(CurlyBrace), traverseEmpty = true)
  private val ExpectedArgListContext = ArgumentListContext(options = ExpectedListTraversalOptions)

  private val typeRenderer = mock[TypeRenderer]
  private val expressionTermRenderer = mock[ExpressionTermRenderer]
  private val termArgumentRenderer = mock[ArgumentRenderer[Term]]
  private val argumentListRenderer = mock[ArgumentListRenderer]

  private val arrayInitializerRenderer = new ArrayInitializerRendererImpl(
    typeRenderer,
    expressionTermRenderer,
    termArgumentRenderer,
    argumentListRenderer
  )

  test("renderWithValues() when has type and values") {
    val tpe = t"T"
    val values = List(q"val1", q"val2")
    val context = ArrayInitializerValuesRenderContext(tpe = tpe, values = values)

    doWrite("T").when(typeRenderer).render(eqTree(tpe))
    doWrite("{ val1, val2 }").when(argumentListRenderer).render(
      eqTreeList(values),
      eqTo(termArgumentRenderer),
      eqArgumentListContext(ExpectedArgListContext)
    )

    arrayInitializerRenderer.renderWithValues(context)

    outputWriter.toString shouldBe "new T[] { val1, val2 }"
  }

  test("renderWithValues() when has type and no values") {
    val tpe = t"T"
    val context = ArrayInitializerValuesRenderContext(tpe = tpe)

    doWrite("T").when(typeRenderer).render(eqTree(tpe))
    doWrite("""{}""").when(argumentListRenderer).render(
      eqTo(Nil),
      eqTo(termArgumentRenderer),
      eqArgumentListContext(ExpectedArgListContext)
    )

    arrayInitializerRenderer.renderWithValues(context)

    outputWriter.toString shouldBe "new T[] {}"
  }

  test("renderWithValues() when has no type but has values should use the Java type 'Object'") {
    val values = List(q"val1", q"val2")
    val context = ArrayInitializerValuesRenderContext(values = values)

    doWrite("Object").when(typeRenderer).render(eqTree(t"Object"))
    doWrite("{ val1, val2 }").when(argumentListRenderer).render(
      eqTreeList(values),
      eqTo(termArgumentRenderer),
      eqArgumentListContext(ExpectedArgListContext)
    )

    arrayInitializerRenderer.renderWithValues(context)

    outputWriter.toString shouldBe "new Object[] { val1, val2 }"
  }

  test("renderWithValues() when has an empty context should use the Java type 'Object'") {
    doWrite("Object").when(typeRenderer).render(eqTree(t"Object"))
    doWrite("""{}""").when(argumentListRenderer).render(
      eqTreeList(Nil),
      eqTo(termArgumentRenderer),
      eqArgumentListContext(ExpectedArgListContext)
    )

    arrayInitializerRenderer.renderWithValues(ArrayInitializerValuesRenderContext())

    outputWriter.toString shouldBe "new Object[] {}"
  }

  test("renderWithSize() when has non-default type and non-default size") {
    val tpe = t"T"
    val size = q"3"
    val context = ArrayInitializerSizeRenderContext(tpe = tpe, size = size)

    doWrite("T").when(typeRenderer).render(eqTree(tpe))
    doWrite("3").when(expressionTermRenderer).render(eqTree(size))

    arrayInitializerRenderer.renderWithSize(context)

    outputWriter.toString shouldBe "new T[3]"
  }

  test("renderWithSize() when has the defaults for both type and size") {
    val size = q"0"

    doWrite("Object").when(typeRenderer).render(eqTree(t"Object"))
    doWrite("0").when(expressionTermRenderer).render(eqTree(size))

    arrayInitializerRenderer.renderWithSize(ArrayInitializerSizeRenderContext())

    outputWriter.toString shouldBe "new Object[0]"

  }

}
