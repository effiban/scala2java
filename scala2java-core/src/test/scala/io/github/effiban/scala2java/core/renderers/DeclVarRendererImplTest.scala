package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ModifiersRenderContext, ValOrVarRenderContext}
import io.github.effiban.scala2java.core.entities.JavaModifier.Private
import io.github.effiban.scala2java.core.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class DeclVarRendererImplTest extends UnitTestSuite {

  private val modListRenderer = mock[ModListRenderer]
  private val typeRenderer = mock[TypeRenderer]
  private val patListRenderer = mock[PatListRenderer]

  private val declVarRenderer = new DeclVarRendererImpl(
    modListRenderer,
    typeRenderer,
    patListRenderer
  )


  test("render()") {
    val declVar = q"private var myVar: Int"
    val javaModifiers = List(Private)

    val expectedModifiersRenderContext = ModifiersRenderContext(
      scalaMods = declVar.mods,
      javaModifiers = javaModifiers
    )
    val valOrVarRenderContext = ValOrVarRenderContext(javaModifiers = javaModifiers)

    doWrite("private ")
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("int").when(typeRenderer).render(eqTree(t"Int"))
    doWrite("myVar").when(patListRenderer).render(eqTreeList(List(p"myVar")))

    declVarRenderer.render(declVar, valOrVarRenderContext)

    outputWriter.toString shouldBe
      """private int myVar""".stripMargin
  }
}

