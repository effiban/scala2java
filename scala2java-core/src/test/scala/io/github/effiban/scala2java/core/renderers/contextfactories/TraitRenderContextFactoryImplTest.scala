package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts.{TemplateBodyRenderContext, TraitRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.TraitRenderContextScalatestMatcher.equalTraitRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{TemplateTraversalResult, TraitTraversalResult}

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TraitRenderContextFactoryImplTest extends UnitTestSuite {

  private val TheJavaModifiers = List(JavaModifier.Public)

  private val ThePermittedSubTypeNames = List(t"A", t"B")

  private val TheTemplateBodyRenderContext = TemplateBodyRenderContext(
    Map(
      q"final var x: Int = 3" -> VarRenderContext()
    )
  )

  private val templateTraversalResult = mock[TemplateTraversalResult]
  private val traitTraversalResult = mock[TraitTraversalResult]

  private val templateBodyRenderContextFactory = mock[TemplateBodyRenderContextFactory]

  private val traitRenderContextFactory = new TraitRenderContextFactoryImpl(templateBodyRenderContextFactory)

  override protected def beforeEach(): Unit = {
    when(traitTraversalResult.templateResult).thenReturn(templateTraversalResult)
    when(templateBodyRenderContextFactory(templateTraversalResult)).thenReturn(TheTemplateBodyRenderContext)
  }

  test("apply() when input has all the Java-specific attributes") {
    val expectedTraitRenderContext = TraitRenderContext(
      TheJavaModifiers,
      ThePermittedSubTypeNames,
      TheTemplateBodyRenderContext
    )

    when(traitTraversalResult.javaModifiers).thenReturn(TheJavaModifiers)

    traitRenderContextFactory(traitTraversalResult, ThePermittedSubTypeNames) should equalTraitRenderContext(expectedTraitRenderContext)
  }

  test("apply() when input has no Java-specific attributes") {
    val expectedTraitRenderContext = TraitRenderContext(bodyContext = TheTemplateBodyRenderContext)

    when(traitTraversalResult.javaModifiers).thenReturn(Nil)

    traitRenderContextFactory(traitTraversalResult) should equalTraitRenderContext(expectedTraitRenderContext)
  }
}
