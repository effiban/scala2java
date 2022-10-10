package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Mod.Annot
import scala.meta.{Init, Name, Type}

class AnnotListTraverserImplTest extends UnitTestSuite {

  private val Annot1 = Annot(Init(tpe = Type.Name("MyAnnot1"), name = Name.Anonymous(), argss = List()))
  private val Annot2 = Annot(Init(tpe = Type.Name("MyAnnot2"), name = Name.Anonymous(), argss = List()))

  private val annotTraverser = mock[AnnotTraverser]

  private val annotListTraverser = new AnnotListTraverserImpl(annotTraverser)

  override def beforeEach(): Unit = {
    super.beforeEach()

    doWrite("@MyAnnot1").when(annotTraverser).traverse(eqTree(Annot1))
    doWrite("@MyAnnot2").when(annotTraverser).traverse(eqTree(Annot2))
  }

  test("traverseAnnotations() when multi-line") {
    annotListTraverser.traverseAnnotations(annotations = List(Annot1, Annot2))

    outputWriter.toString shouldBe
      """@MyAnnot1
        |@MyAnnot2
        |""".stripMargin
  }

  test("traverseAnnotations() when single-line") {
    annotListTraverser.traverseAnnotations(annotations = List(Annot1, Annot2), onSameLine = true)

    outputWriter.toString shouldBe "@MyAnnot1 @MyAnnot2 "
  }
}