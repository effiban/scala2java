package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.JavaStatClassifier
import io.github.effiban.scala2java.core.renderers.contexts.{EmptyDefaultStatRenderContext, TemplateStatRenderContext}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class TemplateStatRendererImplTest extends UnitTestSuite {

  private val enumConstantListRenderer = mock[EnumConstantListRenderer]
  private val defaultStatRenderer = mock[DefaultStatRenderer]
  private val javaStatClassifier = mock[JavaStatClassifier]

  private val templateStatRenderer = new TemplateStatRendererImpl(
    enumConstantListRenderer,
    defaultStatRenderer,
    javaStatClassifier
  )

  test("render() for Decl.Var") {
    val declVar = q"var x: int"

    doWrite("int x").when(defaultStatRenderer).render(eqTree(declVar), eqTo(EmptyDefaultStatRenderContext))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(declVar))).thenReturn(true)

    templateStatRenderer.render(declVar)

    outputWriter.toString shouldBe
      """int x;
        |""".stripMargin
  }

  test("render() for Defn.Var which is not an enum constant list") {
    val defnVar = q"var x: int = 3"

    doWrite("int x = 3").when(defaultStatRenderer).render(eqTree(defnVar), eqTo(EmptyDefaultStatRenderContext))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(defnVar))).thenReturn(true)

    templateStatRenderer.render(defnVar)

    outputWriter.toString shouldBe
      """int x = 3;
        |""".stripMargin
  }

  test("render() for Defn.Var which is an enum constant list") {
    val defnVar = q"final var First, Second = Value"

    doWrite("First, Second").when(enumConstantListRenderer).render(eqTree(defnVar))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(defnVar))).thenReturn(true)

    templateStatRenderer.render(defnVar, TemplateStatRenderContext(enumConstantList = true))

    outputWriter.toString shouldBe
      """First, Second;
        |""".stripMargin
  }

  test("render() for Import should write a comment") {
    val `import` = q"import a.b.c"

    templateStatRenderer.render(`import`)

    outputWriter.toString shouldBe "/* import a.b.c */"
  }

  test("render() for Defn.Def") {
    val defnDef = q"def foo(x: int): int = x + 1"

    doWrite(
      """int foo(int x) {
        |  return x + 1;
        |}
        |""".stripMargin)
      .when(defaultStatRenderer).render(eqTree(defnDef), eqTo(EmptyDefaultStatRenderContext))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(defnDef))).thenReturn(false)

    templateStatRenderer.render(defnDef)

    outputWriter.toString shouldBe
      """int foo(int x) {
        |  return x + 1;
        |}
        |""".stripMargin
  }
}
