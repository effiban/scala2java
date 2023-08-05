package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.{Source, Stat, XtensionQuasiquoteSource, XtensionQuasiquoteTerm}

class SourceTraverserImplTest extends UnitTestSuite {

  private val Pkg1 =
    q"""
    package pkg1 {
      case class Class1(x: Int)
    }
    """
  private val Pkg2 =
    q"""
    package pkg2 {
      case class Class2(y: Int)
    }
    """

  private val TraversedPkg1 =
    q"""
    package tpkg1 {
      case class TClass1(xx: Int)
    }
    """
  private val TraversedPkg2 =
    q"""
    package tpkg2 {
      case class TClass2(yy: Int)
    }
    """

  private val ExcludedImport = q"import scala.abc"

  private val defaultStatTraverser = mock[DefaultStatTraverser]

  private val sourceTraverser = new SourceTraverserImpl(defaultStatTraverser)


  test("traverse() when there are no empty results") {
     val theSource =
      source"""
      package pkg1 {
        case class Class1(x: Int)
      }
      package pkg2 {
        case class Class2(y: Int)
      }
      """

    val expectedSource = Source(List(TraversedPkg1, TraversedPkg2))

    expectTraverseStat()

    sourceTraverser.traverse(theSource).structure shouldBe expectedSource.structure
  }

  test("traverse() when there are empty results should skip them") {
    val theSource =
      source"""
      import scala.abc

      package pkg1 {
        case class Class1(x: Int)
      }
      package pkg2 {
        case class Class2(y: Int)
      }
      """

    val expectedSource = Source(List(TraversedPkg1, TraversedPkg2))

    expectTraverseStat()

    sourceTraverser.traverse(theSource).structure shouldBe expectedSource.structure
  }

  private def expectTraverseStat() = {
    doAnswer((stat: Stat, _: StatContext) => stat match {
      case aStat if aStat.structure == Pkg1.structure => Some(TraversedPkg1)
      case aStat if aStat.structure == Pkg2.structure => Some(TraversedPkg2)
      case aStat if aStat.structure == ExcludedImport.structure => None
      case aStat => aStat
    }).when(defaultStatTraverser).traverse(any[Stat], eqTo(StatContext()))
  }
}
