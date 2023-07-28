package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.matchers.SealedHierarchiesMockitoMatcher.eqSealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.{PkgRenderContext, StatRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.PkgRenderContextScalatestMatcher.equalPkgRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{PkgTraversalResult, PopulatedStatTraversalResult, StatTraversalResult}
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class PkgRenderContextFactoryImplTest extends UnitTestSuite {

  private val defaultStatRenderContextFactory = mock[DefaultStatRenderContextFactory]

  private val pkgRenderContextFactory = new PkgRenderContextFactoryImpl(defaultStatRenderContextFactory)

  test("apply") {
    val statResult1 = mock[PopulatedStatTraversalResult]
    val statResult2 = mock[PopulatedStatTraversalResult]

    val statRenderContext1 = mock[StatRenderContext]
    val statRenderContext2 = mock[StatRenderContext]

    val stat1 = q"class A { def foo(x: Int) = x + 1 }"
    val stat2 = q"class B { def goo(y: Int) = y + 1 }"

    when(statResult1.tree).thenReturn(stat1)
    when(statResult2.tree).thenReturn(stat2)

    val sealedHierarchies = SealedHierarchies(Map(t"ParentOfA" -> List(t"A")))
    val traversalResult = PkgTraversalResult(
      pkgRef = q"a.b",
      statResults = List(statResult1, statResult2),
      sealedHierarchies = sealedHierarchies
    )
    val expectedRenderContext = PkgRenderContext(Map(stat1 -> statRenderContext1, stat2 -> statRenderContext2))

    doAnswer((statResult: StatTraversalResult) => statResult match {
      case aStatResult if aStatResult == statResult1 => statRenderContext1
      case aStatResult if aStatResult == statResult2 => statRenderContext2
    }).when(defaultStatRenderContextFactory)(any[StatTraversalResult], eqSealedHierarchies(sealedHierarchies))

    pkgRenderContextFactory(traversalResult) should equalPkgRenderContext(expectedRenderContext)
  }

}
