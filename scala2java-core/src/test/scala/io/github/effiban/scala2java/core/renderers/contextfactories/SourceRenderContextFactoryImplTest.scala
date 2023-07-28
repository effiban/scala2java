package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.matchers.SealedHierarchiesMockitoMatcher.eqSealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.{SourceRenderContext, StatRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.SourceRenderContextScalatestMatcher.equalSourceRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{PopulatedStatTraversalResult, SourceTraversalResult, StatTraversalResult}
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.XtensionQuasiquoteTerm

class SourceRenderContextFactoryImplTest extends UnitTestSuite {

  private val pkgStatRenderContextFactory = mock[DefaultStatRenderContextFactory]

  private val sourceRenderContextFactory = new SourceRenderContextFactoryImpl(pkgStatRenderContextFactory)

  test("apply") {
    val statResult1 = mock[PopulatedStatTraversalResult]
    val statResult2 = mock[PopulatedStatTraversalResult]

    val statRenderContext1 = mock[StatRenderContext]
    val statRenderContext2 = mock[StatRenderContext]

    val stat1 = q"package a.b.c"
    val stat2 = q"package d.e.f"

    when(statResult1.tree).thenReturn(stat1)
    when(statResult2.tree).thenReturn(stat2)

    val traversalResult = SourceTraversalResult(List(statResult1, statResult2))
    val expectedRenderContext = SourceRenderContext(Map(stat1 -> statRenderContext1, stat2 -> statRenderContext2))

    doAnswer((statResult: StatTraversalResult) => statResult match {
      case aStatResult if aStatResult == statResult1 => statRenderContext1
      case aStatResult if aStatResult == statResult2 => statRenderContext2
    }).when(pkgStatRenderContextFactory)(any[StatTraversalResult], eqSealedHierarchies(SealedHierarchies()))

    sourceRenderContextFactory(traversalResult) should equalSourceRenderContext(expectedRenderContext)
  }

}
