package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ModifiersRenderContext, ValOrVarRenderContext}
import io.github.effiban.scala2java.core.entities.JavaModifier.Private
import io.github.effiban.scala2java.core.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.{eqSomeTree, eqTreeList}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class DefnVarRendererImplTest extends UnitTestSuite {

  private val modListRenderer = mock[ModListRenderer]
  private val defnValOrVarTypeRenderer = mock[DefnValOrVarTypeRenderer]
  private val patListRenderer = mock[PatListRenderer]
  private val expressionTermRenderer = mock[ExpressionTermRenderer]

  private val defnVarRenderer = new DefnVarRendererImpl(
    modListRenderer,
    defnValOrVarTypeRenderer,
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

    val valOrVarRenderContext = ValOrVarRenderContext(javaModifiers = javaModifiers)

    doWrite("private ")
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("int")
      .when(defnValOrVarTypeRenderer).render(eqSomeTree(t"Int"), eqTo(valOrVarRenderContext))
    doWrite("myVar").when(patListRenderer).render(eqTreeList(List(p"myVar")))
    doWrite("3").when(expressionTermRenderer).render(eqTree(q"3"))

    defnVarRenderer.render(defnVar, valOrVarRenderContext)

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

    val valOrVarRenderContext = ValOrVarRenderContext(javaModifiers = javaModifiers)

    doWrite("private ")
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("int")
      .when(defnValOrVarTypeRenderer).render(eqSomeTree(t"Int"), eqTo(valOrVarRenderContext))
    doWrite("myVar").when(patListRenderer).render(eqTreeList(List(p"myVar")))

    defnVarRenderer.render(defnVar, valOrVarRenderContext)

    outputWriter.toString shouldBe
      """private int myVar""".stripMargin
  }

  test("render() when has no type, no Java modifiers, but has RHS") {
    val defnVar = q"var myVar = 3"

    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = defnVar.mods)

    doWrite("")
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("/* UnknownType */")
      .when(defnValOrVarTypeRenderer).render(eqTo(None), eqTo(ValOrVarRenderContext()))
    doWrite("myVar").when(patListRenderer).render(eqTreeList(List(p"myVar")))
    doWrite("3").when(expressionTermRenderer).render(eqTree(q"3"))

    defnVarRenderer.render(defnVar, ValOrVarRenderContext())

    outputWriter.toString shouldBe
      """/* UnknownType */ myVar = 3""".stripMargin
  }
}

