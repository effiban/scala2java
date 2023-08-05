package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedCtorSecondary, EnrichedDefnVar, EnrichedEnumConstantList}
import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.{CtorSecondaryRenderContext, EnumConstantListRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{CtorSecondaryTraversalResult, DefnVarTraversalResult, EnumConstantListTraversalResult}

class TemplateStatRenderContextFactoryImplTest extends UnitTestSuite {

  @deprecated
  private val ctorSecondaryTraversalResult = mock[CtorSecondaryTraversalResult]
  @deprecated
  private val enumConstantListTraversalResult = mock[EnumConstantListTraversalResult]
  @deprecated
  private val defnVarTraversalResult = mock[DefnVarTraversalResult]

  private val enrichedCtorSecondary = mock[EnrichedCtorSecondary]
  private val enrichedEnumConstantList = mock[EnrichedEnumConstantList]
  private val enrichedDefnVar = mock[EnrichedDefnVar]

  private val ctorSecondaryRenderContext = mock[CtorSecondaryRenderContext]
  private val varRenderContext = mock[VarRenderContext]

  private val ctorSecondaryRenderContextFactory = mock[CtorSecondaryRenderContextFactory]
  private val defaultStatRenderContextFactory = mock[DefaultStatRenderContextFactory]

  private val templateStatRenderContextFactory = new TemplateStatRenderContextFactoryImpl(
    ctorSecondaryRenderContextFactory,
    defaultStatRenderContextFactory
  )

  test("apply() for a CtorSecondaryTraversalResult") {
    when(ctorSecondaryRenderContextFactory(ctorSecondaryTraversalResult)).thenReturn(ctorSecondaryRenderContext)
    templateStatRenderContextFactory(ctorSecondaryTraversalResult) shouldBe ctorSecondaryRenderContext
  }

  test("apply() for an EnrichedCtorSecondary") {
    when(ctorSecondaryRenderContextFactory(enrichedCtorSecondary)).thenReturn(ctorSecondaryRenderContext)
    templateStatRenderContextFactory(enrichedCtorSecondary) shouldBe ctorSecondaryRenderContext
  }

  test("apply() for an EnumConstantListTraversalResult") {
    templateStatRenderContextFactory(enumConstantListTraversalResult) shouldBe EnumConstantListRenderContext
  }

  test("apply() for an EnrichedEnumConstantList") {
    templateStatRenderContextFactory(enrichedEnumConstantList) shouldBe EnumConstantListRenderContext
  }

  test("apply() for a DefnVarTraversalResult") {
    when(defaultStatRenderContextFactory(defnVarTraversalResult)).thenReturn(varRenderContext)
    templateStatRenderContextFactory(defnVarTraversalResult) shouldBe varRenderContext
  }

  test("apply() for a EnrichedDefnVar") {
    when(defaultStatRenderContextFactory(enrichedDefnVar, SealedHierarchies())).thenReturn(varRenderContext)
    templateStatRenderContextFactory(enrichedDefnVar) shouldBe varRenderContext
  }
}
