package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Mod.Annot
import scala.meta.{Init, Name, Type}

class AnnotListRendererImplTest extends UnitTestSuite {

  private val Annot1 = Annot(Init(tpe = Type.Name("MyAnnot1"), name = Name.Anonymous(), argss = List()))
  private val Annot2 = Annot(Init(tpe = Type.Name("MyAnnot2"), name = Name.Anonymous(), argss = List()))

  private val annotRenderer = mock[AnnotRenderer]

  private val annotListRenderer = new AnnotListRendererImpl(annotRenderer)

  override def beforeEach(): Unit = {
    super.beforeEach()

    doWrite("@MyAnnot1").when(annotRenderer).render(eqTree(Annot1))
    doWrite("@MyAnnot2").when(annotRenderer).render(eqTree(Annot2))
  }

  test("render() when multi-line") {
    annotListRenderer.render(annotations = List(Annot1, Annot2))

    outputWriter.toString shouldBe
      """@MyAnnot1
        |@MyAnnot2
        |""".stripMargin
  }

  test("render() when single-line") {
    annotListRenderer.render(annotations = List(Annot1, Annot2), onSameLine = true)

    outputWriter.toString shouldBe "@MyAnnot1 @MyAnnot2 "
  }
}
