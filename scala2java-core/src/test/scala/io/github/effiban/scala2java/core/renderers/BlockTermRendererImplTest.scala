package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.JavaStatClassifier
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class BlockTermRendererImplTest extends UnitTestSuite {

  private val expressionTermRefRenderer = mock[ExpressionTermRefRenderer]
  private val defaultTermRenderer = mock[DefaultTermRenderer]
  private val javaStatClassifier = mock[JavaStatClassifier]

  private val blockTermRenderer = new BlockTermRendererImpl(
    expressionTermRefRenderer,
    defaultTermRenderer,
    javaStatClassifier
  )


  test("render() Term.Name") {
    val termName = q"x"

    doWrite("x").when(expressionTermRefRenderer).render(eqTree(termName))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(termName))).thenReturn(true)

    blockTermRenderer.render(termName)

    outputWriter.toString shouldBe
      """x;
       |""".stripMargin
  }

  test("render() Term.Apply") {
    val termApply = q"func(2)"

    doWrite("func(2)").when(defaultTermRenderer).render(eqTree(termApply))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(termApply))).thenReturn(true)

    blockTermRenderer.render(termApply)

    outputWriter.toString shouldBe
      """func(2);
      |""".stripMargin
  }

  test("render() Term.If") {
    val termIf =
      q"""
      if (cond) {
        doSomething()
      } else {
        doSomethingElse()
      }
      """

    doWrite(
      """|if (cond) {
         |  doSomething()
         |} else {
         |  doSomethingElse()
         |}""".stripMargin)
      .when(defaultTermRenderer).render(eqTree(termIf))

    when(javaStatClassifier.requiresEndDelimiter(eqTree(termIf))).thenReturn(false)

    blockTermRenderer.render(termIf)

    outputWriter.toString shouldBe
      """|if (cond) {
         |  doSomething()
         |} else {
         |  doSomethingElse()
         |}""".stripMargin

  }

  test("renderLast() Term.Name with no uncertain return") {
    val termName = q"x"

    doWrite("x").when(expressionTermRefRenderer).render(eqTree(termName))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(termName))).thenReturn(true)

    blockTermRenderer.renderLast(termName)

    outputWriter.toString shouldBe
      """x;
        |""".stripMargin
  }

  test("renderLast() Term.Apply with no uncertain return") {
    val termApply = q"func(2)"

    doWrite("func(2)").when(defaultTermRenderer).render(eqTree(termApply))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(termApply))).thenReturn(true)

    blockTermRenderer.render(termApply)

    outputWriter.toString shouldBe
      """func(2);
        |""".stripMargin
  }

  test("renderLast() Term.If with no uncertain return") {
    val termIf =
      q"""
    if (cond) {
      doSomething()
    } else {
      doSomethingElse()
    }
    """

    doWrite(
      """|if (cond) {
         |  doSomething()
         |} else {
         |  doSomethingElse()
         |}""".stripMargin)
      .when(defaultTermRenderer).render(eqTree(termIf))

    when(javaStatClassifier.requiresEndDelimiter(eqTree(termIf))).thenReturn(false)

    blockTermRenderer.render(termIf)

    outputWriter.toString shouldBe
      """|if (cond) {
         |  doSomething()
         |} else {
         |  doSomethingElse()
         |}""".stripMargin

  }

  test("renderLast() Term.Name when has uncertain return") {
    val termName = q"x"

    doWrite("x").when(expressionTermRefRenderer).render(eqTree(termName))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(termName))).thenReturn(true)

    blockTermRenderer.renderLast(termName, uncertainReturn = true)

    outputWriter.toString shouldBe
      """/* return? */x;
        |""".stripMargin
  }
}
