package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.{CtorSecondaryRenderContext, EnumConstantListRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{CtorSecondaryTraversalResult, DefnVarTraversalResult, EnumConstantListTraversalResult}

class TemplateStatRenderContextFactoryImplTest extends UnitTestSuite {

  private val ctorSecondaryTraversalResult = mock[CtorSecondaryTraversalResult]
  private val enumConstantListTraversalResult = mock[EnumConstantListTraversalResult]
  private val defnVarTraversalResult = mock[DefnVarTraversalResult]

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

  test("apply() for an EnumConstantListTraversalResult") {
    templateStatRenderContextFactory(enumConstantListTraversalResult) shouldBe EnumConstantListRenderContext
  }

  test("apply() for a DefnVarTraversalResult") {
    when(defaultStatRenderContextFactory(defnVarTraversalResult)).thenReturn(varRenderContext)
    templateStatRenderContextFactory(defnVarTraversalResult) shouldBe varRenderContext
  }
}
