package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts.{DefRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{DeclDefTraversalResult, DeclVarTraversalResult}

class DeclRenderContextFactoryImplTest extends UnitTestSuite {

  private val TheJavaModifiers = List(JavaModifier.Public, JavaModifier.Final)

  private val declVarTraversalResult = mock[DeclVarTraversalResult]
  private val declDefTraversalResult = mock[DeclDefTraversalResult]

  test("apply() to DeclVarTraversalResult") {
    when(declVarTraversalResult.javaModifiers).thenReturn(TheJavaModifiers)
    DeclRenderContextFactory(declVarTraversalResult) shouldBe VarRenderContext(TheJavaModifiers)
  }

  test("apply() to DeclDefTraversalResult") {
    when(declDefTraversalResult.javaModifiers).thenReturn(TheJavaModifiers)
    DeclRenderContextFactory(declDefTraversalResult) shouldBe DefRenderContext(TheJavaModifiers)
  }
}
