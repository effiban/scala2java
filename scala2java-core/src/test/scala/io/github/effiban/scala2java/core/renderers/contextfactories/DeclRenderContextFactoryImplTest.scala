package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedDeclDef, EnrichedDeclVar}
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts.{DefRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

class DeclRenderContextFactoryImplTest extends UnitTestSuite {

  private val TheJavaModifiers = List(JavaModifier.Public, JavaModifier.Final)

  private val enrichedDeclVar = mock[EnrichedDeclVar]
  private val enrichedDeclDef = mock[EnrichedDeclDef]

  test("apply() to EnrichedDeclVar") {
    when(enrichedDeclVar.javaModifiers).thenReturn(TheJavaModifiers)
    DeclRenderContextFactory(enrichedDeclVar) shouldBe VarRenderContext(TheJavaModifiers)
  }

  test("apply() to EnrichedDeclDef") {
    when(enrichedDeclDef.javaModifiers).thenReturn(TheJavaModifiers)
    DeclRenderContextFactory(enrichedDeclDef) shouldBe DefRenderContext(TheJavaModifiers)
  }
}
