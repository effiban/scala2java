package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Pkg, Stat, Tree, XtensionQuasiquoteTerm}

class PkgTransformerImplTest extends UnitTestSuite {

  private val Class1 = q"case class Class1(x: X)"
  private val Class2 = q"case class Class2(y: Y)"

  private val TransformedClass1 = q"case class Class1(x: XX)"
  private val TransformedClass2 = q"case class Class2(y: YY)"

  private val Pkg =
    q"""
    package pkg1 {
       case class Class1(x: X)
       case class Class2(y: Y)
    }
    """

  private val TransformedPkg =
    q"""
    package pkg1 {
       case class Class1(x: XX)
       case class Class2(y: YY)
    }
    """

  private val treeTransformer = mock[TreeTransformer]

  private val pkgTransformer = new PkgTransformerImpl(treeTransformer)

  test("transform") {
      doAnswer((stat: Stat) => stat match {
        case aStat: Stat if aStat.structure == Class1.structure => TransformedClass1
        case aStat: Stat if aStat.structure == Class2.structure => TransformedClass2
        case aTree => aTree
      }).when(treeTransformer).transform(any[Tree])

      pkgTransformer.transform(Pkg).structure shouldBe TransformedPkg.structure

  }
}
