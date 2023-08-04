package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.enrichers.entities._
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedPkgStatListScalatestMatcher.equalEnrichedPkgStatList
import io.github.effiban.scala2java.core.entities.{JavaModifier, SealedHierarchies}
import io.github.effiban.scala2java.core.matchers.SealedHierarchiesMockitoMatcher.eqSealedHierarchies
import io.github.effiban.scala2java.core.resolvers.SealedHierarchiesResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Name, Stat, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class PkgStatListEnricherImplTest extends UnitTestSuite {

  private val TheTrait =
    q"""
    trait MyTrait {
      final var x: Int
    }
    """
  private val TheEnrichedTrait = TestableEnrichedTrait(
    defnTrait = TheTrait,
    javaModifiers = List(JavaModifier.Public),
    enrichedStats = List(EnrichedDeclVar(q"final var x: Int"))
  )


  private val TheObject =
    q"""
    object MyObject {
      final var x: Int = 3
    }
    """
  private val TheEnrichedObject = TestableEnrichedObject(
    defnObject = TheObject,
    javaModifiers = List(JavaModifier.Public),
    enrichedStats = List(EnrichedDefnVar(q"final var x: Int = 3"))
  )

  private val TheImport = q"import a.b.c"
  private val TheEnrichedImport = EnrichedSimpleStat(TheImport)

  private val pkgStatEnricher = mock[PkgStatEnricher]
  private val sealedHierarchiesResolver = mock[SealedHierarchiesResolver]

  private val pkgStatListEnricher = new PkgStatListEnricherImpl(
    pkgStatEnricher,
    sealedHierarchiesResolver
  )

  test("enrich()") {
    val stats = List(
      TheImport,
      TheTrait,
      TheObject
    )
    val expectedSealedHierarchies = SealedHierarchies(Map(t"A" -> List(Name.Indeterminate("B"))))
    val expectedEnrichedPkgStatList = EnrichedPkgStatList(
      enrichedStats = List(
        TheEnrichedImport,
        TheEnrichedTrait,
        TheEnrichedObject
      ),
      sealedHierarchies = expectedSealedHierarchies
    )

    when(sealedHierarchiesResolver.traverse(eqTreeList(stats))).thenReturn(expectedSealedHierarchies)

    doAnswer((stat: Stat, _: SealedHierarchies) => stat match {
      case aStat if aStat.structure == TheImport.structure => TheEnrichedImport
      case aStat if aStat.structure == TheTrait.structure => TheEnrichedTrait
      case aStat if aStat.structure == TheObject.structure => TheEnrichedObject
    }).when(pkgStatEnricher).enrich(any[Stat], eqSealedHierarchies(expectedSealedHierarchies))

    pkgStatListEnricher.enrich(stats) should equalEnrichedPkgStatList(expectedEnrichedPkgStatList)
  }
}
