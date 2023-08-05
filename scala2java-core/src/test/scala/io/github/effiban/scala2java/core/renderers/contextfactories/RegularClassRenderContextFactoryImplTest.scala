package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedRegularClass, EnrichedTemplate}
import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}
import io.github.effiban.scala2java.core.renderers.contexts.{RegularClassRenderContext, TemplateBodyRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.RegularClassRenderContextScalatestMatcher.equalRegularClassRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{RegularClassTraversalResult, TemplateTraversalResult}

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class RegularClassRenderContextFactoryImplTest extends UnitTestSuite {

  private val TheJavaModifiers = List(JavaModifier.Public)
  private val TheJavaTypeKeyword = JavaKeyword.Enum
  private val TheJavaInheritanceKeyword = JavaKeyword.Implements
  private val ThePermittedSubTypeNames = List(t"A", t"B")

  private val TheTemplateBodyRenderContext = TemplateBodyRenderContext(
    Map(
      q"final var x: Int = 3" -> VarRenderContext()
    )
  )

  @deprecated
  private val templateTraversalResult = mock[TemplateTraversalResult]
  @deprecated
  private val regularClassTraversalResult = mock[RegularClassTraversalResult]

  private val enrichedTemplate = mock[EnrichedTemplate]
  private val enrichedRegularClass = mock[EnrichedRegularClass]

  private val templateBodyRenderContextFactory = mock[TemplateBodyRenderContextFactory]

  private val regularClassRenderContextFactory = new RegularClassRenderContextFactoryImpl(templateBodyRenderContextFactory)

  override protected def beforeEach(): Unit = {
    when(regularClassTraversalResult.javaTypeKeyword).thenReturn(TheJavaTypeKeyword)
    when(regularClassTraversalResult.templateResult).thenReturn(templateTraversalResult)
    when(templateBodyRenderContextFactory(templateTraversalResult)).thenReturn(TheTemplateBodyRenderContext)

    when(enrichedRegularClass.javaTypeKeyword).thenReturn(TheJavaTypeKeyword)
    when(enrichedRegularClass.enrichedTemplate).thenReturn(enrichedTemplate)
    when(templateBodyRenderContextFactory(enrichedTemplate)).thenReturn(TheTemplateBodyRenderContext)
  }

  test("apply() to RegularClassTraversalResult when input has all Java-specific attributes") {
    val expectedRegularClassRenderContext = RegularClassRenderContext(
      javaModifiers = TheJavaModifiers,
      javaTypeKeyword = TheJavaTypeKeyword,
      maybeInheritanceKeyword = Some(TheJavaInheritanceKeyword),
      permittedSubTypeNames = ThePermittedSubTypeNames,
      bodyContext = TheTemplateBodyRenderContext
    )

    when(regularClassTraversalResult.javaModifiers).thenReturn(TheJavaModifiers)
    when(regularClassTraversalResult.maybeInheritanceKeyword).thenReturn(Some(TheJavaInheritanceKeyword))

    val actualRegularClassRenderContext = regularClassRenderContextFactory(regularClassTraversalResult, ThePermittedSubTypeNames)
    actualRegularClassRenderContext should equalRegularClassRenderContext(expectedRegularClassRenderContext)
  }

  test("apply() to EnrichedRegularClass when input has all Java-specific attributes") {
    val expectedRegularClassRenderContext = RegularClassRenderContext(
      javaModifiers = TheJavaModifiers,
      javaTypeKeyword = TheJavaTypeKeyword,
      maybeInheritanceKeyword = Some(TheJavaInheritanceKeyword),
      permittedSubTypeNames = ThePermittedSubTypeNames,
      bodyContext = TheTemplateBodyRenderContext
    )

    when(enrichedRegularClass.javaModifiers).thenReturn(TheJavaModifiers)
    when(enrichedRegularClass.maybeInheritanceKeyword).thenReturn(Some(TheJavaInheritanceKeyword))

    val actualRegularClassRenderContext = regularClassRenderContextFactory(enrichedRegularClass, ThePermittedSubTypeNames)
    actualRegularClassRenderContext should equalRegularClassRenderContext(expectedRegularClassRenderContext)
  }

  test("apply() to RegularClassTraversalResult when input has mandatory Java-specific attributes only") {
    val expectedRegularClassRenderContext = RegularClassRenderContext(
      javaTypeKeyword = TheJavaTypeKeyword,
      bodyContext = TheTemplateBodyRenderContext
    )

    when(regularClassTraversalResult.javaModifiers).thenReturn(Nil)
    when(regularClassTraversalResult.maybeInheritanceKeyword).thenReturn(None)

    regularClassRenderContextFactory(regularClassTraversalResult) should equalRegularClassRenderContext(expectedRegularClassRenderContext)
  }

  test("apply() to EnrichedRegularClass when input has mandatory Java-specific attributes only") {
    val expectedRegularClassRenderContext = RegularClassRenderContext(
      javaTypeKeyword = TheJavaTypeKeyword,
      bodyContext = TheTemplateBodyRenderContext
    )

    when(enrichedRegularClass.javaModifiers).thenReturn(Nil)
    when(enrichedRegularClass.maybeInheritanceKeyword).thenReturn(None)

    regularClassRenderContextFactory(enrichedRegularClass, Nil) should equalRegularClassRenderContext(expectedRegularClassRenderContext)
  }
}
