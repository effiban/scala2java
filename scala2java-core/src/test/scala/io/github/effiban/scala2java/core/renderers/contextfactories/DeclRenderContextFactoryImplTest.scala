package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedDeclDef, EnrichedDeclVar}
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts.{DefRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{DeclDefTraversalResult, DeclVarTraversalResult}

class DeclRenderContextFactoryImplTest extends UnitTestSuite {

  private val TheJavaModifiers = List(JavaModifier.Public, JavaModifier.Final)

  @deprecated
  private val declVarTraversalResult = mock[DeclVarTraversalResult]
  @deprecated
  private val declDefTraversalResult = mock[DeclDefTraversalResult]

  private val enrichedDeclVar = mock[EnrichedDeclVar]
  private val enrichedDeclDef = mock[EnrichedDeclDef]

  test("apply() to DeclVarTraversalResult") {
    when(declVarTraversalResult.javaModifiers).thenReturn(TheJavaModifiers)
    DeclRenderContextFactory(declVarTraversalResult) shouldBe VarRenderContext(TheJavaModifiers)
  }

  test("apply() to DeclDefTraversalResult") {
    when(declDefTraversalResult.javaModifiers).thenReturn(TheJavaModifiers)
    DeclRenderContextFactory(declDefTraversalResult) shouldBe DefRenderContext(TheJavaModifiers)
  }

  test("apply() to EnrichedDeclVar") {
    when(enrichedDeclVar.javaModifiers).thenReturn(TheJavaModifiers)
    DeclRenderContextFactory(enrichedDeclVar) shouldBe VarRenderContext(TheJavaModifiers)
  }

  test("apply() to EnrichedDeclDef") {
    when(enrichedDeclDef.javaModifiers).thenReturn(TheJavaModifiers)
    DeclRenderContextFactory(enrichedDeclDef) shouldBe DefRenderContext(TheJavaModifiers)
  }
}
