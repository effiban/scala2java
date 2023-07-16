package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.entities.JavaModifier.{Final, Private, Static}
import io.github.effiban.scala2java.core.orderings.JavaModifierOrdering
import io.github.effiban.scala2java.core.renderers.contexts.ModifiersRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Mod, Name, XtensionQuasiquoteMod}

class ModListRendererImplTest extends UnitTestSuite {

  private val annotListRenderer = mock[AnnotListRenderer]

  private val modListRenderer = new ModListRendererImpl(annotListRenderer, TestJavaModifierOrdering)

  test("render when has nothing") {
    modListRenderer.render(ModifiersRenderContext())

    outputWriter.toString shouldBe ""
  }

  test("render when has annotations only and not on same line") {
    val annots = List(mod"@MyAnnot1", mod"@MyAnnot2")
    val context = ModifiersRenderContext(scalaMods = annots)

    doWrite(
      """@MyAnnot1
        |@MyAnnot2
        |""".stripMargin)
      .when(annotListRenderer).render(eqTreeList(annots), onSameLine = eqTo(false))

    modListRenderer.render(context)

    outputWriter.toString shouldBe
      """@MyAnnot1
        |@MyAnnot2
        |""".stripMargin
  }

  test("render when has annotations only and on same line") {
    val annots = List(mod"@MyAnnot1", mod"@MyAnnot2")
    val context = ModifiersRenderContext(scalaMods = annots, annotsOnSameLine = true)

    doWrite("@MyAnnot1 @MyAnnot2")
      .when(annotListRenderer).render(eqTreeList(annots), onSameLine = eqTo(true))

    modListRenderer.render(context)

    outputWriter.toString shouldBe "@MyAnnot1 @MyAnnot2"
  }

  test("render when `implicit` and nothing else") {
    val scalaMods = List(mod"implicit")
    val context = ModifiersRenderContext(scalaMods = scalaMods)

    modListRenderer.render(context)

    outputWriter.toString shouldBe "/* implicit */"
  }

  test("render when has non-annotation modifiers only, and out of order") {
    val context = ModifiersRenderContext(
      scalaMods = List(Mod.Private(Name.Anonymous())),
      javaModifiers = List(Static, Private, Final)
    )

    modListRenderer.render(context)

    outputWriter.toString shouldBe "private static final "
  }

  test("render when has everything") {
    val annots = List(mod"@MyAnnot1", mod"@MyAnnot2")
    val scalaMods = annots :+ mod"private" :+ mod"implicit"
    val javaModifiers = List(Static, Final, Private)
    val context = ModifiersRenderContext(
      scalaMods = scalaMods,
      annotsOnSameLine = true,
      javaModifiers = javaModifiers
    )

    doWrite(
      """@MyAnnot1
        |@MyAnnot2
        |""".stripMargin)
      .when(annotListRenderer).render(eqTreeList(annots), onSameLine = eqTo(true))

    modListRenderer.render(context)

    outputWriter.toString shouldBe
      """@MyAnnot1
        |@MyAnnot2
        |/* implicit */private static final """.stripMargin
  }

  private object TestJavaModifierOrdering extends JavaModifierOrdering {

    private final val JavaModifierToPosition: Map[JavaModifier, Int] = Map(
      JavaModifier.Private -> 0,
      JavaModifier.Static -> 1,
      JavaModifier.Final -> 2
    )

    override def compare(modifier1: JavaModifier, modifier2: JavaModifier): Int = positionOf(modifier1) - positionOf(modifier2)

    private def positionOf(modifier: JavaModifier) = JavaModifierToPosition.getOrElse(modifier, Int.MaxValue)
  }
}
