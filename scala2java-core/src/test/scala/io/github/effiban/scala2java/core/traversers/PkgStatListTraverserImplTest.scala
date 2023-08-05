package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Stat, XtensionQuasiquoteTerm}

class PkgStatListTraverserImplTest extends UnitTestSuite {

  private val TheTrait = q"trait MyTrait { final var x: Int }"
  private val TheTraversedTrait = q"trait MyTraversedTrait { final var xx: Int }"

  private val TheObject = q"object MyObject { val x: Int = 3 } "
  private val TheTraversedObject = q"object MyTraversedObject { val xx: Int = 3 } "

  private val IncludedImport = q"import a.b.c"
  private val TraversedIncludedImport = q"import d.e.f"

  private val ExcludedImport1 = q"import g.h.i"
  private val ExcludedImport2 = q"import j.k.l"


  private val pkgStatTraverser = mock[PkgStatTraverser]

  private val pkgStatListTraverser = new PkgStatListTraverserImpl(pkgStatTraverser)

  test("traverse() when there are no empty stats returned") {
    val stats = List(
      IncludedImport,
      TheTrait,
      TheObject
    )
    val expectedPkgStats = List(
      TraversedIncludedImport,
      TheTraversedTrait,
      TheTraversedObject
    )

    expectTraverseStat()

    pkgStatListTraverser.traverse(stats).structure shouldBe expectedPkgStats.structure
  }

  test("traverse() when there are empty stats returned should skip them") {
    val stats = List(
      ExcludedImport1,
      IncludedImport,
      ExcludedImport2,
      TheTrait,
      TheObject
    )
    val expectedPkgStats = List(
      TraversedIncludedImport,
      TheTraversedTrait,
      TheTraversedObject
    )

    expectTraverseStat()

    pkgStatListTraverser.traverse(stats).structure shouldBe expectedPkgStats.structure
  }

  private def expectTraverseStat() = {
    doAnswer((stat: Stat) => stat match {
      case aStat if aStat.structure == IncludedImport.structure => Some(TraversedIncludedImport)
      case aStat if aStat.structure == TheTrait.structure => Some(TheTraversedTrait)
      case aStat if aStat.structure == TheObject.structure => Some(TheTraversedObject)
      case _ => None
    }).when(pkgStatTraverser).traverse(any[Stat])
  }
}
