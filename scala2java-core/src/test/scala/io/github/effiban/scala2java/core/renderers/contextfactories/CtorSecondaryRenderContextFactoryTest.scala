package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts.CtorSecondaryRenderContext
import io.github.effiban.scala2java.core.renderers.matchers.CtorSecondaryRenderContextScalatestMatcher.equalCtorSecondaryRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.CtorSecondaryTraversalResult

import scala.meta.XtensionQuasiquoteType

class CtorSecondaryRenderContextFactoryTest extends UnitTestSuite {

  private val TheClassName = t"MyClass"
  private val TheJavaModifiers = List(JavaModifier.Public, JavaModifier.Final)

  private val ctorSecondaryTraversalResult = mock[CtorSecondaryTraversalResult]

  test("apply()") {
    val expectedRenderContext = CtorSecondaryRenderContext(TheClassName, TheJavaModifiers)

    when(ctorSecondaryTraversalResult.className).thenReturn(TheClassName)
    when(ctorSecondaryTraversalResult.javaModifiers).thenReturn(TheJavaModifiers)

    CtorSecondaryRenderContextFactory(ctorSecondaryTraversalResult) should equalCtorSecondaryRenderContext(expectedRenderContext)
  }
}
