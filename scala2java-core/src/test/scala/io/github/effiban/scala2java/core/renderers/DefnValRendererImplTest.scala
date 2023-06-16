package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ModifiersRenderContext, ValOrVarRenderContext}
import io.github.effiban.scala2java.core.entities.JavaModifier.{Final, Private}
import io.github.effiban.scala2java.core.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.{eqSomeTree, eqTreeList}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class DefnValRendererImplTest extends UnitTestSuite {

  private val modListRenderer = mock[ModListRenderer]
  private val defnValOrVarTypeRenderer = mock[DefnValOrVarTypeRenderer]
  private val patListRenderer = mock[PatListRenderer]
  private val expressionTermRenderer = mock[ExpressionTermRenderer]

  private val defnValRenderer = new DefnValRendererImpl(
    modListRenderer,
    defnValOrVarTypeRenderer,
    patListRenderer,
    expressionTermRenderer
  )


  test("render() when has type and Java modifiers") {
    val defnVal = q"private val myVal: Int = 3"
    val javaModifiers = List(Private, Final)

    val expectedModifiersRenderContext = ModifiersRenderContext(
      scalaMods = defnVal.mods,
      javaModifiers = javaModifiers
    )

    val valOrVarRenderContext = ValOrVarRenderContext(javaModifiers = javaModifiers)

    doWrite("private final ")
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("int")
      .when(defnValOrVarTypeRenderer).render(eqSomeTree(t"Int"), eqTo(valOrVarRenderContext))
    doWrite("myVal").when(patListRenderer).render(eqTreeList(List(p"myVal")))
    doWrite("3").when(expressionTermRenderer).render(eqTree(q"3"))

    defnValRenderer.render(defnVal, valOrVarRenderContext)

    outputWriter.toString shouldBe
      """private final int myVal = 3""".stripMargin
  }

  test("render() when has no type and no Java modifiers") {
    val defnVal = q"val myVal = 3"

    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = defnVal.mods)

    doWrite("")
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("/* UnknownType */")
      .when(defnValOrVarTypeRenderer).render(eqTo(None), eqTo(ValOrVarRenderContext()))
    doWrite("myVal").when(patListRenderer).render(eqTreeList(List(p"myVal")))
    doWrite("3").when(expressionTermRenderer).render(eqTree(q"3"))

    defnValRenderer.render(defnVal, ValOrVarRenderContext())

    outputWriter.toString shouldBe
      """/* UnknownType */ myVal = 3""".stripMargin
  }
}

