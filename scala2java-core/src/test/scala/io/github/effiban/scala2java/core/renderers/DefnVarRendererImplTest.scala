package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.JavaModifier.Private
import io.github.effiban.scala2java.core.renderers.contexts.{ModifiersRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.ModifiersRenderContextMockitoMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.{eqSomeTree, eqTreeList}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class DefnVarRendererImplTest extends UnitTestSuite {

  private val modListRenderer = mock[ModListRenderer]
  private val defnVarTypeRenderer = mock[DefnVarTypeRenderer]
  private val patListRenderer = mock[PatListRenderer]
  private val expressionTermRenderer = mock[ExpressionTermRenderer]

  private val defnVarRenderer = new DefnVarRendererImpl(
    modListRenderer,
    defnVarTypeRenderer,
    patListRenderer,
    expressionTermRenderer
  )


  test("render() when has type, Java modifiers and RHS") {
    val defnVar = q"private var myVar: Int = 3"
    val javaModifiers = List(Private)

    val expectedModifiersRenderContext = ModifiersRenderContext(
      scalaMods = defnVar.mods,
      javaModifiers = javaModifiers
    )

    val varRenderContext = VarRenderContext(javaModifiers = javaModifiers)

    doWrite("private ")
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("int")
      .when(defnVarTypeRenderer).render(eqSomeTree(t"Int"), eqTo(varRenderContext))
    doWrite("myVar").when(patListRenderer).render(eqTreeList(List(p"myVar")))
    doWrite("3").when(expressionTermRenderer).render(eqTree(q"3"))

    defnVarRenderer.render(defnVar, varRenderContext)

    outputWriter.toString shouldBe
      """private int myVar = 3""".stripMargin
  }

  test("render() when has type, Java modifiers and no RHS") {
    val defnVar = q"private var myVar: Int = _"
    val javaModifiers = List(Private)

    val expectedModifiersRenderContext = ModifiersRenderContext(
      scalaMods = defnVar.mods,
      javaModifiers = javaModifiers
    )

    val varRenderContext = VarRenderContext(javaModifiers = javaModifiers)

    doWrite("private ")
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("int")
      .when(defnVarTypeRenderer).render(eqSomeTree(t"Int"), eqTo(varRenderContext))
    doWrite("myVar").when(patListRenderer).render(eqTreeList(List(p"myVar")))

    defnVarRenderer.render(defnVar, varRenderContext)

    outputWriter.toString shouldBe
      """private int myVar""".stripMargin
  }

  test("render() when has no type, no Java modifiers, but has RHS") {
    val defnVar = q"var myVar = 3"

    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = defnVar.mods)

    doWrite("")
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("/* UnknownType */")
      .when(defnVarTypeRenderer).render(eqTo(None), eqTo(VarRenderContext()))
    doWrite("myVar").when(patListRenderer).render(eqTreeList(List(p"myVar")))
    doWrite("3").when(expressionTermRenderer).render(eqTree(q"3"))

    defnVarRenderer.render(defnVar, VarRenderContext())

    outputWriter.toString shouldBe
      """/* UnknownType */ myVar = 3""".stripMargin
  }
}

