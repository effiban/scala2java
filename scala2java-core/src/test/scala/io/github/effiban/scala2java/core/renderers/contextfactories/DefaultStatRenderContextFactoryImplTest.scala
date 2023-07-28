package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.{DeclRenderContext, PkgRenderContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{DeclTraversalResult, PkgTraversalResult}

import scala.meta.XtensionQuasiquoteType

class DefaultStatRenderContextFactoryImplTest extends UnitTestSuite {
  private val TheSealedHierarchies = SealedHierarchies(
    Map(
      t"A" -> List(t"A1", t"A2")
    )
  )


  private val pkgTraversalResult = mock[PkgTraversalResult]
  private val declTraversalResult = mock[DeclTraversalResult]

  private val pkgRenderContext = mock[PkgRenderContext]
  private val declRenderContext = mock[DeclRenderContext]

  private val pkgRenderContextFactory = mock[PkgRenderContextFactory]
  private val declRenderContextFactory = mock[DeclRenderContextFactory]

  private val defaultStatRenderContextFactory = new DefaultStatRenderContextFactoryImpl(pkgRenderContextFactory, declRenderContextFactory)

  test("apply() for a PkgTraversalResult") {
    when(pkgRenderContextFactory(pkgTraversalResult)).thenReturn(pkgRenderContext)

    defaultStatRenderContextFactory(pkgTraversalResult, TheSealedHierarchies) shouldBe pkgRenderContext
  }

  test("apply() for a DeclTraversalResult") {
    when(declRenderContextFactory(declTraversalResult)).thenReturn(declRenderContext)

    defaultStatRenderContextFactory(declTraversalResult, TheSealedHierarchies) shouldBe declRenderContext
  }
}
