package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.CtorSecondaryRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.CtorSecondaryTraversalResult

class TemplateStatRenderContextFactoryImplTest extends UnitTestSuite {

  private val ctorSecondaryTraversalResult = mock[CtorSecondaryTraversalResult]

  private val ctorSecondaryRenderContext = mock[CtorSecondaryRenderContext]

  private val ctorSecondaryRenderContextFactory = mock[CtorSecondaryRenderContextFactory]

  private val templateStatRenderContextFactory = new TemplateStatRenderContextFactoryImpl(ctorSecondaryRenderContextFactory)

  test("apply() for a Ctor.Secondary") {
    when(ctorSecondaryRenderContextFactory(ctorSecondaryTraversalResult)).thenReturn(ctorSecondaryRenderContext)

    templateStatRenderContextFactory(ctorSecondaryTraversalResult) shouldBe ctorSecondaryRenderContext
  }
}
