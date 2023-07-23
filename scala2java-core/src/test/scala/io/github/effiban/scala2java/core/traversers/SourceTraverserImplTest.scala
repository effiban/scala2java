package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{EmptyStatTraversalResult, PkgTraversalResult, SourceTraversalResult}
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.{Stat, XtensionQuasiquoteSource, XtensionQuasiquoteTerm}

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

  private val ExcludedImport = q"import scala.abc"

  private val pkg1TraversalResult = mock[PkgTraversalResult]
  private val pkg2TraversalResult = mock[PkgTraversalResult]

  private val defaultStatTraverser = mock[DefaultStatTraverser]

  private val sourceTraverser = new SourceTraverserImpl(defaultStatTraverser)


  test("traverse() when there are no empty results") {
     val TheSource =
      source"""
      package pkg1 {
        case class Class1(x: Int)
      }
      package pkg2 {
        case class Class2(y: Int)
      }
      """

    val expectedSourceTraversalResult = SourceTraversalResult(List(pkg1TraversalResult, pkg2TraversalResult))

    doAnswer((stat: Stat, _: StatContext) => stat match {
      case aStat if aStat.structure == Pkg1.structure => pkg1TraversalResult
      case aStat if aStat.structure == Pkg2.structure => pkg2TraversalResult
    }).when(defaultStatTraverser).traverse(any[Stat], eqTo(StatContext()))

    sourceTraverser.traverse(TheSource) shouldBe expectedSourceTraversalResult
  }

  test("traverse() when there are empty results should skip them") {
    val TheSource =
      source"""
      import scala.abc

      package pkg1 {
        case class Class1(x: Int)
      }
      package pkg2 {
        case class Class2(y: Int)
      }
      """

    val expectedSourceTraversalResult = SourceTraversalResult(List(pkg1TraversalResult, pkg2TraversalResult))

    doAnswer((stat: Stat, _: StatContext) => stat match {
      case aStat if aStat.structure == ExcludedImport.structure => EmptyStatTraversalResult
      case aStat if aStat.structure == Pkg1.structure => pkg1TraversalResult
      case aStat if aStat.structure == Pkg2.structure => pkg2TraversalResult
    }).when(defaultStatTraverser).traverse(any[Stat], eqTo(StatContext()))


    sourceTraverser.traverse(TheSource) shouldBe expectedSourceTraversalResult
  }
}
