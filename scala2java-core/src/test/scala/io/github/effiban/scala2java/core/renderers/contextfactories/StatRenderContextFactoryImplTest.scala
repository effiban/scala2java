package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.PkgRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.PkgTraversalResult

class StatRenderContextFactoryImplTest extends UnitTestSuite {
  private val pkgTraversalResult = mock[PkgTraversalResult]
  private val pkgRenderContext = mock[PkgRenderContext]

  private val pkgRenderContextFactory = mock[PkgRenderContextFactory]

  private val statRenderContextFactory = new StatRenderContextFactoryImpl(pkgRenderContextFactory)

  test("apply() for a PkgTraversalResult") {
    when(pkgRenderContextFactory(pkgTraversalResult)).thenReturn(pkgRenderContext)

    statRenderContextFactory(pkgTraversalResult) shouldBe pkgRenderContext
  }
}
