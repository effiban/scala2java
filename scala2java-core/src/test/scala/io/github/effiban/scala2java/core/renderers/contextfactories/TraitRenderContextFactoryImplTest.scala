package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedTemplate, EnrichedTrait}
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts.{TemplateBodyRenderContext, TraitRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.TraitRenderContextScalatestMatcher.equalTraitRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TraitRenderContextFactoryImplTest extends UnitTestSuite {

  private val TheJavaModifiers = List(JavaModifier.Public)

  private val ThePermittedSubTypeNames = List(t"A", t"B")

  private val TheTemplateBodyRenderContext = TemplateBodyRenderContext(
    Map(
      q"final var x: Int = 3" -> VarRenderContext()
    )
  )

  private val enrichedTemplate = mock[EnrichedTemplate]
  private val enrichedTrait = mock[EnrichedTrait]

  private val templateBodyRenderContextFactory = mock[TemplateBodyRenderContextFactory]

  private val traitRenderContextFactory = new TraitRenderContextFactoryImpl(templateBodyRenderContextFactory)

  override protected def beforeEach(): Unit = {
    when(enrichedTrait.enrichedTemplate).thenReturn(enrichedTemplate)
    when(templateBodyRenderContextFactory(enrichedTemplate)).thenReturn(TheTemplateBodyRenderContext)
  }

  test("apply() to EnrichedTrait when input has all the Java-specific attributes") {
    val expectedTraitRenderContext = TraitRenderContext(
      TheJavaModifiers,
      ThePermittedSubTypeNames,
      TheTemplateBodyRenderContext
    )

    when(enrichedTrait.javaModifiers).thenReturn(TheJavaModifiers)

    traitRenderContextFactory(enrichedTrait, ThePermittedSubTypeNames) should equalTraitRenderContext(expectedTraitRenderContext)
  }

  test("apply() to EnrichedTrait when input has no Java-specific attributes") {
    val expectedTraitRenderContext = TraitRenderContext(bodyContext = TheTemplateBodyRenderContext)

    when(enrichedTrait.javaModifiers).thenReturn(Nil)

    traitRenderContextFactory(enrichedTrait, Nil) should equalTraitRenderContext(expectedTraitRenderContext)
  }
}
