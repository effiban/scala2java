package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteSource

class SourceTransformerImplTest extends UnitTestSuite {

  private val Source =
    source"""
    package pkg1 {
      case class Class1(x: X)
    }
    package pkg2 {
      case class Class2(y: Y)
    }
    """

  private val TransformedSource =
    source"""
    package pkg1 {
      case class Class1(x: XX)
    }
    package pkg2 {
      case class Class2(y: YY)
    }
    """

  private val treeTransformer = mock[TreeTransformer]

  private val sourceTransformer = new SourceTransformerImpl(treeTransformer)

  test("transform") {
    doReturn(TransformedSource).when(treeTransformer).transform(Source)

    sourceTransformer.transform(Source).structure shouldBe TransformedSource.structure
  }
}
