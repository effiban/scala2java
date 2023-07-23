package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.{JavaModifier, SealedHierarchies}
import io.github.effiban.scala2java.core.matchers.SealedHierarchiesMockitoMatcher.eqSealedHierarchies
import io.github.effiban.scala2java.core.resolvers.SealedHierarchiesResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results._
import io.github.effiban.scala2java.core.traversers.results.matchers.PkgStatListTraversalResultScalatestMatcher.equalPkgStatListTraversalResult
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
  private val TheTraitTraversalResult = TestableTraitTraversalResult(
    defnTrait = TheTrait,
    javaModifiers = List(JavaModifier.Public),
    statResults = List(DeclVarTraversalResult(q"final var x: Int"))
  )


  private val TheObject =
    q"""
    object MyObject {
      final var x: Int = 3
    }
    """
  private val TheObjectTraversalResult = TestableObjectTraversalResult(
    defnObject = TheObject,
    javaModifiers = List(JavaModifier.Public),
    statResults = List(DefnVarTraversalResult(q"final var x: Int = 3"))
  )

  private val IncludedImport = q"import a.b.c"
  private val IncludedImportTraversalResult = SimpleStatTraversalResult(IncludedImport)

  private val ExcludedImport1 = q"import d.e.f"
  private val ExcludedImport2 = q"import g.h.i"


  private val pkgStatTraverser = mock[PkgStatTraverser]
  private val sealedHierarchiesResolver = mock[SealedHierarchiesResolver]

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
    val expectedPkgStatListResult = PkgStatListTraversalResult(
      statResults = List(
        IncludedImportTraversalResult,
        TheTraitTraversalResult,
        TheObjectTraversalResult
      ),
      sealedHierarchies = expectedSealedHierarchies
    )

    when(sealedHierarchiesResolver.traverse(eqTreeList(stats))).thenReturn(expectedSealedHierarchies)

    doAnswer((stat: Stat, _: SealedHierarchies) => stat match {
      case IncludedImport => IncludedImportTraversalResult
      case TheTrait => TheTraitTraversalResult
      case TheObject => TheObjectTraversalResult
    }).when(pkgStatTraverser).traverse(any[Stat], eqSealedHierarchies(expectedSealedHierarchies))

    pkgStatListTraverser.traverse(stats) should equalPkgStatListTraversalResult(expectedPkgStatListResult)
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
    val expectedPkgStatListResult = PkgStatListTraversalResult(
      statResults = List(
        IncludedImportTraversalResult,
        TheTraitTraversalResult,
        TheObjectTraversalResult
      ),
      sealedHierarchies = expectedSealedHierarchies
    )

    when(sealedHierarchiesResolver.traverse(eqTreeList(stats))).thenReturn(expectedSealedHierarchies)

    doAnswer((stat: Stat, _: SealedHierarchies) => stat match {
      case ExcludedImport1 | ExcludedImport2 => EmptyStatTraversalResult
      case IncludedImport => IncludedImportTraversalResult
      case TheTrait => TheTraitTraversalResult
      case TheObject => TheObjectTraversalResult
    }).when(pkgStatTraverser).traverse(any[Stat], eqSealedHierarchies(expectedSealedHierarchies))

    pkgStatListTraverser.traverse(stats) should equalPkgStatListTraversalResult(expectedPkgStatListResult)
  }
}
