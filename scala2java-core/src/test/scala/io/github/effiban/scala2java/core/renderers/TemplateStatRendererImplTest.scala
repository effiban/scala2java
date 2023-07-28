package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.JavaStatClassifier
import io.github.effiban.scala2java.core.renderers.contexts.{CtorSecondaryRenderContext, DefRenderContext, EnumConstantListRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TemplateStatRendererImplTest extends UnitTestSuite {

  private val enumConstantListRenderer = mock[EnumConstantListRenderer]
  private val ctorSecondaryRenderer = mock[CtorSecondaryRenderer]
  private val defaultStatRenderer = mock[DefaultStatRenderer]
  private val javaStatClassifier = mock[JavaStatClassifier]

  private val templateStatRenderer = new TemplateStatRendererImpl(
    enumConstantListRenderer,
    ctorSecondaryRenderer,
    defaultStatRenderer,
    javaStatClassifier
  )

  test("render() for Decl.Var") {
    val declVar = q"var x: int"
    val context = VarRenderContext()

    doWrite("int x").when(defaultStatRenderer).render(eqTree(declVar), eqTo(context))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(declVar))).thenReturn(true)

    templateStatRenderer.render(declVar, context)

    outputWriter.toString shouldBe
      """int x;
        |""".stripMargin
  }

  test("render() for Defn.Var which is not an enum constant list") {
    val defnVar = q"var x: int = 3"
    val context = VarRenderContext()

    doWrite("int x = 3").when(defaultStatRenderer).render(eqTree(defnVar), eqTo(context))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(defnVar))).thenReturn(true)

    templateStatRenderer.render(defnVar, context)

    outputWriter.toString shouldBe
      """int x = 3;
        |""".stripMargin
  }

  test("render() for Defn.Var which is an enum constant list") {
    val defnVar = q"final var First, Second = Value"

    doWrite("First, Second").when(enumConstantListRenderer).render(eqTree(defnVar))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(defnVar))).thenReturn(true)

    templateStatRenderer.render(defnVar, EnumConstantListRenderContext)

    outputWriter.toString shouldBe
      """First, Second;
        |""".stripMargin
  }

  test("render() for Import should write a commented statement") {
    val `import` = q"import a.b.c"

    templateStatRenderer.render(`import`)

    outputWriter.toString shouldBe
      """/* import a.b.c */;
        |""".stripMargin
  }

  test("render() for Ctor.Secondary which has a correct context") {
    val ctorSecondary =
      q"""
      def this(x: int) = {
         this()
         this.x = x
      }
      """
    val context = CtorSecondaryRenderContext(t"MyClass")

    doWrite(
      """MyClass(final int x) {
        |  this();
        |  this.x = x;
        |}
        |""".stripMargin)
      .when(ctorSecondaryRenderer).render(eqTree(ctorSecondary), eqTo(context))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(ctorSecondary))).thenReturn(false)

    templateStatRenderer.render(ctorSecondary, context)

    outputWriter.toString shouldBe
      """MyClass(final int x) {
        |  this();
        |  this.x = x;
        |}
        |""".stripMargin
  }

  test("render() for Ctor.Secondary which has an incorrect context should throw exception") {
    val ctorSecondary =
      q"""
      def this(x: int) = {
         this()
         this.x = x
      }
      """

    intercept[IllegalStateException] {
      templateStatRenderer.render(ctorSecondary)
    }
  }

  test("render() for Defn.Def") {
    val defnDef = q"def foo(x: int): int = x + 1"
    val context = DefRenderContext()

    doWrite(
      """int foo(int x) {
        |  return x + 1;
        |}
        |""".stripMargin)
      .when(defaultStatRenderer).render(eqTree(defnDef), eqTo(context))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(defnDef))).thenReturn(false)

    templateStatRenderer.render(defnDef, context)

    outputWriter.toString shouldBe
      """int foo(int x) {
        |  return x + 1;
        |}
        |""".stripMargin
  }
}
