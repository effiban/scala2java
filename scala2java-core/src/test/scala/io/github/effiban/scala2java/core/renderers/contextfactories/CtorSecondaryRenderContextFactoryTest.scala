package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedCtorSecondary
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts.CtorSecondaryRenderContext
import io.github.effiban.scala2java.core.renderers.matchers.CtorSecondaryRenderContextScalatestMatcher.equalCtorSecondaryRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteType

class CtorSecondaryRenderContextFactoryTest extends UnitTestSuite {

  private val TheClassName = t"MyClass"
  private val TheJavaModifiers = List(JavaModifier.Public, JavaModifier.Final)

  private val enrichedCtorSecondary = mock[EnrichedCtorSecondary]

  test("apply() to EnrichedCtorSecondary") {
    val expectedRenderContext = CtorSecondaryRenderContext(TheClassName, TheJavaModifiers)

    when(enrichedCtorSecondary.className).thenReturn(TheClassName)
    when(enrichedCtorSecondary.javaModifiers).thenReturn(TheJavaModifiers)

    CtorSecondaryRenderContextFactory(enrichedCtorSecondary) should equalCtorSecondaryRenderContext(expectedRenderContext)
  }
}
