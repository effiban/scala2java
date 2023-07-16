package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.{JavaStatClassifier, TermTreeClassifier}
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts.{BlockStatRenderContext, IfRenderContext, TryRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class BlockStatRendererImplTest extends UnitTestSuite {

  private val statTermRenderer = mock[StatTermRenderer]
  private val ifRenderer = mock[IfRenderer]
  private val tryRenderer = mock[TryRenderer]
  private val tryWithHandlerRenderer = mock[TryWithHandlerRenderer]
  private val defnVarRenderer = mock[DefnVarRenderer]
  private val declVarRenderer = mock[DeclVarRenderer]
  private val termTreeClassifier: TermTreeClassifier = mock[TermTreeClassifier]
  private val javaStatClassifier = mock[JavaStatClassifier]

  private val blockStatRenderer = new BlockStatRendererImpl(
    statTermRenderer,
    ifRenderer,
    tryRenderer,
    tryWithHandlerRenderer,
    defnVarRenderer,
    declVarRenderer,
    termTreeClassifier,
    javaStatClassifier
  )


  test("render() Term.Name") {
    val termName = q"x"

    doWrite("x").when(statTermRenderer).render(eqTree(termName))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(termName))).thenReturn(true)

    blockStatRenderer.render(termName)

    outputWriter.toString shouldBe
      """x;
       |""".stripMargin
  }

  test("render() Term.Apply") {
    val termApply = q"func(2)"

    doWrite("func(2)").when(statTermRenderer).render(eqTree(termApply))
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
      .when(statTermRenderer).render(eqTree(termIf))

    when(javaStatClassifier.requiresEndDelimiter(eqTree(termIf))).thenReturn(false)

    blockStatRenderer.render(termIf)

    outputWriter.toString shouldBe
      """|if (cond) {
         |  doSomething()
         |} else {
         |  doSomethingElse()
         |}""".stripMargin

  }

  test("render() Defn.Var when 'final'") {
    val defnVar = q"final var x: Int = 3"
    val expectedRenderContext = VarRenderContext(javaModifiers = List(JavaModifier.Final), inBlock = true)

    doWrite("final int x = 3").when(defnVarRenderer).render(eqTree(defnVar), eqTo(expectedRenderContext))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(defnVar))).thenReturn(true)

    blockStatRenderer.render(defnVar)

    outputWriter.toString shouldBe
      """final int x = 3;
        |""".stripMargin
  }

  test("render() Defn.Var when not 'final'") {
    val defnVar = q"var x: Int = 3"
    val expectedRenderContext = VarRenderContext(inBlock = true)

    doWrite("int x = 3").when(defnVarRenderer).render(eqTree(defnVar), eqTo(expectedRenderContext))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(defnVar))).thenReturn(true)

    blockStatRenderer.render(defnVar)

    outputWriter.toString shouldBe
      """int x = 3;
        |""".stripMargin
  }

  test("render() Decl.Var") {
    val declVar = q"var x: Int"
    val expectedRenderContext = VarRenderContext(inBlock = true)

    doWrite("int x").when(declVarRenderer).render(eqTree(declVar), eqTo(expectedRenderContext))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(declVar))).thenReturn(true)

    blockStatRenderer.render(declVar)

    outputWriter.toString shouldBe
      """int x;
        |""".stripMargin
  }

  test("renderLast() Term.Name when has no uncertain return should not add comment") {
    val termName = q"x"

    doWrite("x").when(statTermRenderer).render(eqTree(termName))
    when(termTreeClassifier.isReturnable(eqTree(termName))).thenReturn(true)
    when(javaStatClassifier.requiresEndDelimiter(eqTree(termName))).thenReturn(true)

    blockStatRenderer.renderLast(termName)

    outputWriter.toString shouldBe
      """x;
        |""".stripMargin
  }

  test("renderLast() Term.Name when has uncertain return should add comment") {
    val termName = q"x"

    doWrite("x").when(statTermRenderer).render(eqTree(termName))
    when(termTreeClassifier.isReturnable(eqTree(termName))).thenReturn(true)
    when(javaStatClassifier.requiresEndDelimiter(eqTree(termName))).thenReturn(true)

    blockStatRenderer.renderLast(termName, BlockStatRenderContext(uncertainReturn = true))

    outputWriter.toString shouldBe
      """/* return? */x;
        |""".stripMargin
  }

  test("renderLast() Term.Apply when has no uncertain return should not add comment") {
    val termApply = q"func(2)"

    doWrite("func(2)").when(statTermRenderer).render(eqTree(termApply))
    when(termTreeClassifier.isReturnable(eqTree(termApply))).thenReturn(true)
    when(javaStatClassifier.requiresEndDelimiter(eqTree(termApply))).thenReturn(true)

    blockStatRenderer.renderLast(termApply)

    outputWriter.toString shouldBe
      """func(2);
        |""".stripMargin
  }

  test("renderLast() Term.Throw when has uncertain return should not add comment") {
    val termThrow = q"throw new IllegalStateException()"

    doWrite("throw new IllegalStateException()").when(statTermRenderer).render(eqTree(termThrow))
    when(termTreeClassifier.isReturnable(eqTree(termThrow))).thenReturn(false)
    when(javaStatClassifier.requiresEndDelimiter(eqTree(termThrow))).thenReturn(true)

    blockStatRenderer.renderLast(termThrow, BlockStatRenderContext(uncertainReturn = true))

    outputWriter.toString shouldBe
      """throw new IllegalStateException();
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

  test("renderLast() Term.If when both clauses have uncertain return") {
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

    blockStatRenderer.renderLast(termIf, context = BlockStatRenderContext(uncertainReturn = true))

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

  test("renderLast() Term.Try when both clauses have uncertain return") {
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

    blockStatRenderer.renderLast(termTry, BlockStatRenderContext(uncertainReturn = true))

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
      .when(tryWithHandlerRenderer).render(eqTree(tryWithHandler), eqTo(TryRenderContext()))

    blockStatRenderer.renderLast(tryWithHandler)

    outputWriter.toString shouldBe
      """|try {
         |  /* return? */doSomething();
         |}
         |/* UNPARSEABLE catch handler: someCatchHandler */
         |""".stripMargin
  }
}
