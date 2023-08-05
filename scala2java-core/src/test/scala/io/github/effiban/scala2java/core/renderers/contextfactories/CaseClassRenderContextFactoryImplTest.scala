package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedCaseClass, EnrichedTemplate}
import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}
import io.github.effiban.scala2java.core.renderers.contexts.{CaseClassRenderContext, TemplateBodyRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.CaseClassRenderContextScalatestMatcher.equalCaseClassRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{CaseClassTraversalResult, TemplateTraversalResult}

import scala.meta.XtensionQuasiquoteTerm

class CaseClassRenderContextFactoryImplTest extends UnitTestSuite {

  private val TheJavaModifiers = List(JavaModifier.Public)
  private val TheJavaInheritanceKeyword = JavaKeyword.Implements

  private val TheTemplateBodyRenderContext = TemplateBodyRenderContext(
    Map(
      q"final var x: Int = 3" -> VarRenderContext()
    )
  )

  @deprecated
  private val templateTraversalResult = mock[TemplateTraversalResult]
  @deprecated
  private val caseClassTraversalResult = mock[CaseClassTraversalResult]

  private val enrichedTemplate = mock[EnrichedTemplate]
  private val enrichedCaseClass = mock[EnrichedCaseClass]

  private val templateBodyRenderContextFactory = mock[TemplateBodyRenderContextFactory]

  private val caseClassRenderContextFactory = new CaseClassRenderContextFactoryImpl(templateBodyRenderContextFactory)

  override protected def beforeEach(): Unit = {
    when(caseClassTraversalResult.templateResult).thenReturn(templateTraversalResult)
    when(templateBodyRenderContextFactory(templateTraversalResult)).thenReturn(TheTemplateBodyRenderContext)

    when(enrichedCaseClass.enrichedTemplate).thenReturn(enrichedTemplate)
    when(templateBodyRenderContextFactory(enrichedTemplate)).thenReturn(TheTemplateBodyRenderContext)
  }

  test("apply() to CaseClassTraversalResult when input has all Java-specific attributes") {
    val expectedCaseClassRenderContext = CaseClassRenderContext(
      javaModifiers = TheJavaModifiers,
      maybeInheritanceKeyword = Some(TheJavaInheritanceKeyword),
      bodyContext = TheTemplateBodyRenderContext
    )

    when(caseClassTraversalResult.javaModifiers).thenReturn(TheJavaModifiers)
    when(caseClassTraversalResult.maybeInheritanceKeyword).thenReturn(Some(TheJavaInheritanceKeyword))

    caseClassRenderContextFactory(caseClassTraversalResult) should equalCaseClassRenderContext(expectedCaseClassRenderContext)
  }

  test("apply() to EnrichedCaseClass when input has all Java-specific attributes") {
    val expectedCaseClassRenderContext = CaseClassRenderContext(
      javaModifiers = TheJavaModifiers,
      maybeInheritanceKeyword = Some(TheJavaInheritanceKeyword),
      bodyContext = TheTemplateBodyRenderContext
    )

    when(enrichedCaseClass.javaModifiers).thenReturn(TheJavaModifiers)
    when(enrichedCaseClass.maybeInheritanceKeyword).thenReturn(Some(TheJavaInheritanceKeyword))

    caseClassRenderContextFactory(enrichedCaseClass) should equalCaseClassRenderContext(expectedCaseClassRenderContext)
  }

  test("apply() to CaseClassTraversalResult when input has no Java-specific attributes") {
    val expectedCaseClassRenderContext = CaseClassRenderContext(bodyContext = TheTemplateBodyRenderContext)

    when(caseClassTraversalResult.javaModifiers).thenReturn(Nil)
    when(caseClassTraversalResult.maybeInheritanceKeyword).thenReturn(None)

    caseClassRenderContextFactory(caseClassTraversalResult) should equalCaseClassRenderContext(expectedCaseClassRenderContext)
  }

  test("apply() to EnrichedCaseClass when input has no Java-specific attributes") {
    val expectedCaseClassRenderContext = CaseClassRenderContext(bodyContext = TheTemplateBodyRenderContext)

    when(enrichedCaseClass.javaModifiers).thenReturn(Nil)
    when(enrichedCaseClass.maybeInheritanceKeyword).thenReturn(None)

    caseClassRenderContextFactory(enrichedCaseClass) should equalCaseClassRenderContext(expectedCaseClassRenderContext)
  }
}
