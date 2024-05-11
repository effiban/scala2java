package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Pkg, Tree, XtensionQuasiquoteSource, XtensionQuasiquoteTerm}

class SourceTransformerImplTest extends UnitTestSuite {

  private val Pkg1 =
    q"""
    package pkg1 {
      case class Class1(x: X)
    }
    """
  private val Pkg2 =
    q"""
    package pkg2 {
      case class Class2(y: Y)
    }
    """

  private val Source =
    source"""
    package pkg1 {
      case class Class1(x: X)
    }
    package pkg2 {
      case class Class2(y: Y)
    }
    """

  private val TransformedPkg1 =
    q"""
    package pkg1 {
      case class Class1(x: XX)
    }
    """
  private val TransformedPkg2 =
    q"""
    package pkg2 {
      case class Class2(y: YY)
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
    doAnswer((tree: Tree) => tree match {
      case aTree: Pkg if aTree.structure == Pkg1.structure => TransformedPkg1
      case aTree: Pkg if aTree.structure == Pkg2.structure => TransformedPkg2
      case aTree => aTree
    }).when(treeTransformer).transform(any[Tree])

    sourceTransformer.transform(Source).structure shouldBe TransformedSource.structure
  }
}
