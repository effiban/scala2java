package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.{DeclRenderContext, PkgRenderContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{DeclTraversalResult, PkgTraversalResult}

class StatRenderContextFactoryImplTest extends UnitTestSuite {
  private val pkgTraversalResult = mock[PkgTraversalResult]
  private val declTraversalResult = mock[DeclTraversalResult]

  private val pkgRenderContext = mock[PkgRenderContext]
  private val declRenderContext = mock[DeclRenderContext]

  private val pkgRenderContextFactory = mock[PkgRenderContextFactory]
  private val declRenderContextFactory = mock[DeclRenderContextFactory]

  private val statRenderContextFactory = new StatRenderContextFactoryImpl(pkgRenderContextFactory, declRenderContextFactory)

  test("apply() for a PkgTraversalResult") {
    when(pkgRenderContextFactory(pkgTraversalResult)).thenReturn(pkgRenderContext)

    statRenderContextFactory(pkgTraversalResult) shouldBe pkgRenderContext
  }

  test("apply() for a DeclTraversalResult") {
    when(declRenderContextFactory(declTraversalResult)).thenReturn(declRenderContext)

    statRenderContextFactory(declTraversalResult) shouldBe declRenderContext
  }
}
