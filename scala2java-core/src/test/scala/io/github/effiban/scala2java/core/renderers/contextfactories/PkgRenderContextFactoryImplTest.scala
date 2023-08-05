package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.EnrichedPkg
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedStat
import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.matchers.SealedHierarchiesMockitoMatcher.eqSealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.{PkgRenderContext, StatRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.PkgRenderContextScalatestMatcher.equalPkgRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class PkgRenderContextFactoryImplTest extends UnitTestSuite {

  private val defaultStatRenderContextFactory = mock[DefaultStatRenderContextFactory]

  private val pkgRenderContextFactory = new PkgRenderContextFactoryImpl(defaultStatRenderContextFactory)


  test("apply() to EnrichedPkg") {
    val enrichedStat1 = mock[EnrichedStat]
    val enrichedStat2 = mock[EnrichedStat]

    val statRenderContext1 = mock[StatRenderContext]
    val statRenderContext2 = mock[StatRenderContext]

    val stat1 = q"class A { def foo(x: Int) = x + 1 }"
    val stat2 = q"class B { def goo(y: Int) = y + 1 }"

    when(enrichedStat1.stat).thenReturn(stat1)
    when(enrichedStat2.stat).thenReturn(stat2)

    val sealedHierarchies = SealedHierarchies(Map(t"ParentOfA" -> List(t"A")))
    val enrichedPkg = EnrichedPkg(
      pkgRef = q"a.b",
      enrichedStats = List(enrichedStat1, enrichedStat2),
      sealedHierarchies = sealedHierarchies
    )
    val expectedRenderContext = PkgRenderContext(Map(stat1 -> statRenderContext1, stat2 -> statRenderContext2))

    doAnswer((enrichedStat: EnrichedStat) => enrichedStat match {
      case aEnrichedStat if aEnrichedStat == enrichedStat1 => statRenderContext1
      case aEnrichedStat if aEnrichedStat == enrichedStat2 => statRenderContext2
    }).when(defaultStatRenderContextFactory)(any[EnrichedStat], eqSealedHierarchies(sealedHierarchies))

    pkgRenderContextFactory(enrichedPkg) should equalPkgRenderContext(expectedRenderContext)
  }
}
