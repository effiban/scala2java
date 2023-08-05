package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedSource, EnrichedStat}
import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.matchers.SealedHierarchiesMockitoMatcher.eqSealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.{SourceRenderContext, StatRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.SourceRenderContextScalatestMatcher.equalSourceRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.XtensionQuasiquoteTerm

class SourceRenderContextFactoryImplTest extends UnitTestSuite {

  private val defaultStatRenderContextFactory = mock[DefaultStatRenderContextFactory]

  private val sourceRenderContextFactory = new SourceRenderContextFactoryImpl(defaultStatRenderContextFactory)

  test("apply to EnrichedSource") {
    val enrichedStat1 = mock[EnrichedStat]
    val enrichedStat2 = mock[EnrichedStat]

    val statRenderContext1 = mock[StatRenderContext]
    val statRenderContext2 = mock[StatRenderContext]

    val stat1 = q"package a.b.c"
    val stat2 = q"package d.e.f"

    when(enrichedStat1.stat).thenReturn(stat1)
    when(enrichedStat2.stat).thenReturn(stat2)

    val enrichedSource = EnrichedSource(List(enrichedStat1, enrichedStat2))
    val expectedRenderContext = SourceRenderContext(Map(stat1 -> statRenderContext1, stat2 -> statRenderContext2))

    doAnswer((enrichedStat: EnrichedStat) => enrichedStat match {
      case aEnrichedStat if aEnrichedStat == enrichedStat1 => statRenderContext1
      case aEnrichedStat if aEnrichedStat == enrichedStat2 => statRenderContext2
    }).when(defaultStatRenderContextFactory)(any[EnrichedStat], eqSealedHierarchies(SealedHierarchies()))

    sourceRenderContextFactory(enrichedSource) should equalSourceRenderContext(expectedRenderContext)
  }
}
