package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ModifiersRenderContext
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.entities.JavaModifier.{Final, Private, Static}
import io.github.effiban.scala2java.core.orderings.JavaModifierOrdering
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteMod

class ModListRendererImplTest extends UnitTestSuite {

  private val annotListRenderer = mock[AnnotListRenderer]

  private val modListRenderer = new ModListRendererImpl(annotListRenderer, TestJavaModifierOrdering)

  test("render when has nothing") {
    modListRenderer.render(ModifiersRenderContext())

    outputWriter.toString shouldBe ""
  }

  test("render when has annotations only and not on same line") {
    val context = ModifiersRenderContext(annots = List(mod"@MyAnnot1", mod"@MyAnnot2"))

    doWrite(
      """@MyAnnot1
        |@MyAnnot2
        |""".stripMargin)
      .when(annotListRenderer).render(eqTreeList(context.annots), onSameLine = eqTo(false))

    modListRenderer.render(context)

    outputWriter.toString shouldBe
      """@MyAnnot1
        |@MyAnnot2
        |""".stripMargin
  }

  test("render when has annotations only and on same line") {
    val context = ModifiersRenderContext(annots = List(mod"@MyAnnot1", mod"@MyAnnot2"), annotsOnSameLine = true)

    doWrite("@MyAnnot1 @MyAnnot2")
      .when(annotListRenderer).render(eqTreeList(context.annots), onSameLine = eqTo(true))

    modListRenderer.render(context)

    outputWriter.toString shouldBe "@MyAnnot1 @MyAnnot2"
  }

  test("render when hasImplicit = true and nothing else") {
    val context = ModifiersRenderContext(hasImplicit = true)

    modListRenderer.render(context)

    outputWriter.toString shouldBe "/* implicit */"
  }

  test("render when has Java modifiers only and out of order") {
    val context = ModifiersRenderContext(javaModifiers = List(Static, Private, Final))

    modListRenderer.render(context)

    outputWriter.toString shouldBe "private static final "
  }

  test("render when has everything") {
    val context = ModifiersRenderContext(
      annots = List(mod"@MyAnnot1", mod"@MyAnnot2"),
      annotsOnSameLine = true,
      hasImplicit = true,
      javaModifiers = List(Static, Final, Private)
    )

    doWrite("@MyAnnot1 @MyAnnot2")
      .when(annotListRenderer).render(eqTreeList(context.annots), onSameLine = eqTo(true))

    modListRenderer.render(context)

    outputWriter.toString shouldBe "@MyAnnot1 @MyAnnot2/* implicit */private static final "
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
