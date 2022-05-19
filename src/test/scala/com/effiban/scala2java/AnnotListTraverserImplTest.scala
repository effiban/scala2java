package com.effiban.scala2java

import org.mockito.ArgumentMatchers.any

import scala.meta.Mod.Annot
import scala.meta.{Init, Name, Type}

class AnnotListTraverserImplTest extends UnitTestSuite {

  private val annotTraverser = mock[AnnotTraverser]

  private val annotListTraverser = new AnnotListTraverserImpl(annotTraverser)

  override def beforeEach(): Unit = {
    super.beforeEach()
    doAnswer((annot: Annot) => outputWriter.write(annot.toString())).when(annotTraverser).traverse(any[Annot])
  }

  test("traverseAnnotations() when multi-line") {
    annotListTraverser.traverseAnnotations(
      annotations = List(
        Annot(Init(tpe = Type.Name("MyAnnot1"), name = Name.Anonymous(), argss = List())),
        Annot(Init(tpe = Type.Name("MyAnnot2"), name = Name.Anonymous(), argss = List())),
      )
    )

    outputWriter.toString shouldBe
      """@MyAnnot1
        |@MyAnnot2
        |""".stripMargin
  }

  test("traverseAnnotations() when single-line") {
    annotListTraverser.traverseAnnotations(
      annotations = List(
        Annot(Init(tpe = Type.Name("MyAnnot1"), name = Name.Anonymous(), argss = List())),
        Annot(Init(tpe = Type.Name("MyAnnot2"), name = Name.Anonymous(), argss = List())),
      ),
      onSameLine = true
    )

    outputWriter.toString shouldBe "@MyAnnot1 @MyAnnot2 "
  }
}
