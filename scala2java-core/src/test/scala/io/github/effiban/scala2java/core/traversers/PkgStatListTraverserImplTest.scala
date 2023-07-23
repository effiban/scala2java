package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.matchers.SealedHierarchiesMatcher.eqSealedHierarchies
import io.github.effiban.scala2java.core.resolvers.SealedHierarchiesResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results._
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Name, Stat, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class PkgStatListTraverserImplTest extends UnitTestSuite {

  private val TheTrait =
    q"""
    trait MyTrait {
      final var x: Int
    }
    """

  private val TheObject =
    q"""
    object MyObject {
      val x: Int = 3
    }
    """

  private val IncludedImport = q"import a.b.c"
  private val ExcludedImport1 = q"import d.e.f"
  private val ExcludedImport2 = q"import g.h.i"


  private val pkgStatTraverser = mock[PkgStatTraverser]
  private val sealedHierarchiesResolver = mock[SealedHierarchiesResolver]

  private val importTraversalResult = mock[SimpleStatTraversalResult]
  private val traitTraversalResult = mock[TraitTraversalResult]
  private val objectTraversalResult = mock[ObjectTraversalResult]

  private val pkgStatListTraverser = new PkgStatListTraverserImpl(
    pkgStatTraverser,
    sealedHierarchiesResolver
  )

  test("traverse() when there are no empty results") {
    val stats = List(
      IncludedImport,
      TheTrait,
      TheObject
    )
    val expectedSealedHierarchies = SealedHierarchies(Map(t"A" -> List(Name.Indeterminate("B"))))
    val expectedMultiResult = MultiStatTraversalResult(List(
      importTraversalResult,
      traitTraversalResult,
      objectTraversalResult)
    )

    when(sealedHierarchiesResolver.traverse(eqTreeList(stats))).thenReturn(expectedSealedHierarchies)

    doAnswer((stat: Stat, _: SealedHierarchies) => stat match {
      case IncludedImport => importTraversalResult
      case TheTrait => traitTraversalResult
      case TheObject => objectTraversalResult
    }).when(pkgStatTraverser).traverse(any[Stat], eqSealedHierarchies(expectedSealedHierarchies))

    pkgStatListTraverser.traverse(stats) shouldBe expectedMultiResult
  }

  test("traverse() when there are empty results should skip them") {
    val stats = List(
      ExcludedImport1,
      IncludedImport,
      ExcludedImport2,
      TheTrait,
      TheObject
    )
    val expectedSealedHierarchies = SealedHierarchies(Map(t"A" -> List(Name.Indeterminate("B"))))
    val expectedMultiResult = MultiStatTraversalResult(List(
      importTraversalResult,
      traitTraversalResult,
      objectTraversalResult)
    )

    when(sealedHierarchiesResolver.traverse(eqTreeList(stats))).thenReturn(expectedSealedHierarchies)

    doAnswer((stat: Stat, _: SealedHierarchies) => stat match {
      case ExcludedImport1 | ExcludedImport2 => EmptyStatTraversalResult
      case IncludedImport => importTraversalResult
      case TheTrait => traitTraversalResult
      case TheObject => objectTraversalResult
    }).when(pkgStatTraverser).traverse(any[Stat], eqSealedHierarchies(expectedSealedHierarchies))

    pkgStatListTraverser.traverse(stats) shouldBe expectedMultiResult
  }
}
