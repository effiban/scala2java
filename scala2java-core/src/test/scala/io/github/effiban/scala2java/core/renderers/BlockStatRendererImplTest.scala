package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.JavaStatClassifier
import io.github.effiban.scala2java.core.contexts.{IfRenderContext, TryRenderContext}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class BlockStatRendererImplTest extends UnitTestSuite {

  private val expressionTermRefRenderer = mock[ExpressionTermRefRenderer]
  private val defaultTermRenderer = mock[DefaultTermRenderer]
  private val ifRenderer = mock[IfRenderer]
  private val tryRenderer = mock[TryRenderer]
  private val tryWithHandlerRenderer = mock[TryWithHandlerRenderer]
  private val javaStatClassifier = mock[JavaStatClassifier]

  private val blockStatRenderer = new BlockStatRendererImpl(
    expressionTermRefRenderer,
    ifRenderer,
    tryRenderer,
    tryWithHandlerRenderer,
    defaultTermRenderer,
    javaStatClassifier
  )


  test("render() Term.Name") {
    val termName = q"x"

    doWrite("x").when(expressionTermRefRenderer).render(eqTree(termName))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(termName))).thenReturn(true)

    blockStatRenderer.render(termName)

    outputWriter.toString shouldBe
      """x;
       |""".stripMargin
  }

  test("render() Term.Apply") {
    val termApply = q"func(2)"

    doWrite("func(2)").when(defaultTermRenderer).render(eqTree(termApply))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(termApply))).thenReturn(true)

    blockStatRenderer.render(termApply)

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

    blockStatRenderer.render(termIf)

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

    blockStatRenderer.renderLast(termName)

    outputWriter.toString shouldBe
      """x;
        |""".stripMargin
  }

  test("renderLast() Term.Name when has uncertain return") {
    val termName = q"x"

    doWrite("x").when(expressionTermRefRenderer).render(eqTree(termName))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(termName))).thenReturn(true)

    blockStatRenderer.renderLast(termName, uncertainReturn = true)

    outputWriter.toString shouldBe
      """/* return? */x;
        |""".stripMargin
  }

  test("renderLast() Term.Apply with no uncertain return") {
    val termApply = q"func(2)"

    doWrite("func(2)").when(defaultTermRenderer).render(eqTree(termApply))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(termApply))).thenReturn(true)

    blockStatRenderer.render(termApply)

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
         |  doSomething();
         |} else {
         |  doSomethingElse();
         |}""".stripMargin)
      .when(ifRenderer).render(eqTree(termIf), eqTo(IfRenderContext()))

    blockStatRenderer.renderLast(termIf)

    outputWriter.toString shouldBe
      """|if (cond) {
         |  doSomething();
         |} else {
         |  doSomethingElse();
         |}""".stripMargin

  }

  test("renderLast() Term.If when has uncertain return") {
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
         |  /* return? */doSomething();
         |} else {
         |  /* return? */doSomethingElse();
         |}""".stripMargin)
      .when(ifRenderer).render(eqTree(termIf), eqTo(IfRenderContext(uncertainReturn = true)))

    blockStatRenderer.renderLast(termIf, uncertainReturn = true)

    outputWriter.toString shouldBe
      """|if (cond) {
         |  /* return? */doSomething();
         |} else {
         |  /* return? */doSomethingElse();
         |}""".stripMargin

  }

  test("renderLast() Term.Try with no uncertain return") {
    val termTry =
      q"""
      try {
        doSomething()
      } catch {
        case e: Throwable => doSomethingElse()
      }
      """

    doWrite(
      """|try {
         |  doSomething();
         |} catch (Throwable e) {
         |  doSomethingElse();
         |}""".stripMargin)
      .when(tryRenderer).render(eqTree(termTry), eqTo(TryRenderContext()))

    blockStatRenderer.renderLast(termTry)

    outputWriter.toString shouldBe
      """|try {
         |  doSomething();
         |} catch (Throwable e) {
         |  doSomethingElse();
         |}""".stripMargin
  }

  test("renderLast() Term.Try when has uncertain return") {
    val termTry =
      q"""
      try {
        doSomething()
      } catch {
        case e: Throwable => doSomethingElse()
      }
      """

    doWrite(
      """|try {
         |  /* return? */doSomething();
         |} catch (Throwable e) {
         |  /* return? */doSomethingElse();
         |}""".stripMargin)
      .when(tryRenderer).render(eqTree(termTry), eqTo(TryRenderContext(uncertainReturn = true)))

    blockStatRenderer.renderLast(termTry, uncertainReturn = true)

    outputWriter.toString shouldBe
      """|try {
         |  /* return? */doSomething();
         |} catch (Throwable e) {
         |  /* return? */doSomethingElse();
         |}""".stripMargin
  }

  test("renderLast() TryWithHandler with no uncertain return") {
    val tryWithHandler =
      q"""
      try {
        doSomething()
      } catch(someCatchHandler)
      """

    doWrite(
      """|try {
         |  doSomething();
         |}
         |/* UNPARSEABLE catch handler: someCatchHandler */
         |""".stripMargin)
      .when(tryWithHandlerRenderer).render(eqTree(tryWithHandler), eqTo(TryRenderContext()))

    blockStatRenderer.renderLast(tryWithHandler)

    outputWriter.toString shouldBe
      """|try {
         |  doSomething();
         |}
         |/* UNPARSEABLE catch handler: someCatchHandler */
         |""".stripMargin
  }

  test("renderLast() TryWithHandler when has uncertain return") {
    val tryWithHandler =
      q"""
      try {
        doSomething()
      } catch(someCatchHandler)
      """

    doWrite(
      """|try {
         |  /* return? */doSomething();
         |}
         |/* UNPARSEABLE catch handler: someCatchHandler */
         |""".stripMargin)
      .when(tryWithHandlerRenderer).render(eqTree(tryWithHandler), eqTo(TryRenderContext(uncertainReturn = true)))

    blockStatRenderer.renderLast(tryWithHandler, uncertainReturn = true)

    outputWriter.toString shouldBe
      """|try {
         |  /* return? */doSomething();
         |}
         |/* UNPARSEABLE catch handler: someCatchHandler */
         |""".stripMargin
  }
}
