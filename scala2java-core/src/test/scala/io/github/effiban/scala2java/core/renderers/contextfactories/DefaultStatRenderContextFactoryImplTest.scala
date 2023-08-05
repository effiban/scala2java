package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.EnrichedPkg
import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedDecl, EnrichedDefn}
import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.matchers.SealedHierarchiesMockitoMatcher.eqSealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.{DeclRenderContext, DefnRenderContext, PkgRenderContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteType

class DefaultStatRenderContextFactoryImplTest extends UnitTestSuite {
  private val TheSealedHierarchies = SealedHierarchies(
    Map(
      t"A" -> List(t"A1", t"A2")
    )
  )


  private val enrichedPkg = mock[EnrichedPkg]
  private val enrichedDecl = mock[EnrichedDecl]
  private val enrichedDefn = mock[EnrichedDefn]

  private val pkgRenderContext = mock[PkgRenderContext]
  private val declRenderContext = mock[DeclRenderContext]
  private val defnRenderContext = mock[DefnRenderContext]

  private val pkgRenderContextFactory = mock[PkgRenderContextFactory]
  private val declRenderContextFactory = mock[DeclRenderContextFactory]
  private val defnRenderContextFactory = mock[DefnRenderContextFactory]

  private val defaultStatRenderContextFactory = new DefaultStatRenderContextFactoryImpl(
    pkgRenderContextFactory,
    declRenderContextFactory,
    defnRenderContextFactory
  )

  test("apply() for an EnrichedPkg") {
    when(pkgRenderContextFactory(enrichedPkg)).thenReturn(pkgRenderContext)

    defaultStatRenderContextFactory(enrichedPkg, TheSealedHierarchies) shouldBe pkgRenderContext
  }

  test("apply() for an EnrichedDecl") {
    when(declRenderContextFactory(enrichedDecl)).thenReturn(declRenderContext)

    defaultStatRenderContextFactory(enrichedDecl) shouldBe declRenderContext
  }

  test("apply() for an EnrichedDefn") {
    when(defnRenderContextFactory(eqTo(enrichedDefn), eqSealedHierarchies(SealedHierarchies()))).thenReturn(defnRenderContext)

    defaultStatRenderContextFactory(enrichedDefn) shouldBe defnRenderContext
  }
}
