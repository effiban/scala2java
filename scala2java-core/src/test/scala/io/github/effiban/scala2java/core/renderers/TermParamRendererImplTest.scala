package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.JavaModifier.Final
import io.github.effiban.scala2java.core.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.renderers.contexts.ModifiersRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TermParamRendererImplTest extends UnitTestSuite {

  private val Annot1 = mod"@MyAnnot1"
  private val Annot2 = mod"@MyAnnot2"
  private val ParamName = q"myParam"

  private val modListRenderer = mock[ModListRenderer]
  private val typeRenderer = mock[TypeRenderer]
  private val nameRenderer = mock[NameRenderer]

  private val termParamRenderer = new TermParamRendererImpl(
    modListRenderer,
    typeRenderer,
    nameRenderer
  )

  test("render with name only") {
    val termParam = Term.Param(
      mods = Nil,
      name = ParamName,
      decltpe = None,
      default = None
    )

    val expectedModifiersRenderContext = ModifiersRenderContext(annotsOnSameLine = true)

    doWrite("").when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("myParam").when(nameRenderer).render(eqTree(ParamName))

    termParamRenderer.render(termParam)

    outputWriter.toString shouldBe "myParam"
  }

  test("render with name and annotations only") {
    val scalaMods = List(Annot1, Annot2)

    val termParam = Term.Param(
      mods = scalaMods,
      name = ParamName,
      decltpe = None,
      default = None
    )

    val expectedModifiersRenderContext = ModifiersRenderContext(
      scalaMods = scalaMods,
      annotsOnSameLine = true,
      javaModifiers = Nil
    )

    doWrite("@Annot1 @Annot2 ")
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("myParam").when(nameRenderer).render(eqTree(ParamName))

    termParamRenderer.render(termParam)

    outputWriter.toString shouldBe "@Annot1 @Annot2 myParam"
  }

  test("render with name and annotations and 'final' modifier only") {
    val scalaMods = List(Annot1, Annot2, mod"final")
    val javaModifiers = List(Final)

    val termParam = Term.Param(
      mods = scalaMods,
      name = ParamName,
      decltpe = None,
      default = None
    )

    val expectedModifiersRenderContext = ModifiersRenderContext(
      scalaMods = scalaMods,
      annotsOnSameLine = true,
      javaModifiers = javaModifiers
    )

    doWrite("@Annot1 @Annot2 final ")
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("myParam").when(nameRenderer).render(eqTree(ParamName))

    termParamRenderer.render(termParam)

    outputWriter.toString shouldBe "@Annot1 @Annot2 final myParam"
  }

  test("render with name and type only") {
    val termParam = Term.Param(
      mods = Nil,
      name = ParamName,
      decltpe = Some(t"Int"),
      default = None
    )

    val expectedModifiersRenderContext = ModifiersRenderContext(annotsOnSameLine = true)

    doWrite("").when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("int").when(typeRenderer).render(eqTree(t"Int"))
    doWrite("myParam").when(nameRenderer).render(eqTree(ParamName))

    termParamRenderer.render(termParam)

    outputWriter.toString shouldBe "int myParam"
  }

  test("render with name and default only") {
    val termParam = Term.Param(
      mods = Nil,
      name = ParamName,
      decltpe = None,
      default = Some(q"3")
    )

    val expectedModifiersRenderContext = ModifiersRenderContext(annotsOnSameLine = true)

    doWrite("").when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("myParam").when(nameRenderer).render(eqTree(ParamName))

    termParamRenderer.render(termParam)

    outputWriter.toString shouldBe "myParam/* = 3 */"
  }

  test("render with everything") {
    val scalaMods = List(Annot1, Annot2, mod"final")
    val javaModifiers = List(Final)

    val termParam = Term.Param(
      mods = scalaMods,
      name = ParamName,
      decltpe = Some(t"Int"),
      default = Some(q"3")
    )

    val expectedModifiersRenderContext = ModifiersRenderContext(
      scalaMods = scalaMods,
      annotsOnSameLine = true,
      javaModifiers = javaModifiers
    )

    doWrite("@Annot1 @Annot2 final ")
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("int").when(typeRenderer).render(eqTree(t"Int"))
    doWrite("myParam").when(nameRenderer).render(eqTree(ParamName))

    termParamRenderer.render(termParam)

    outputWriter.toString shouldBe "@Annot1 @Annot2 final int myParam/* = 3 */"
  }

}
