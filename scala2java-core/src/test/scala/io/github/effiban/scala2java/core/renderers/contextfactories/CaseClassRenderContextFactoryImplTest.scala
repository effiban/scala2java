package io.github.effiban.scala2java.core.renderers.contextfactories

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

  private val templateTraversalResult = mock[TemplateTraversalResult]
  private val caseClassTraversalResult = mock[CaseClassTraversalResult]

  private val templateBodyRenderContextFactory = mock[TemplateBodyRenderContextFactory]

  private val caseClassRenderContextFactory = new CaseClassRenderContextFactoryImpl(templateBodyRenderContextFactory)

  override protected def beforeEach(): Unit = {
    when(caseClassTraversalResult.templateResult).thenReturn(templateTraversalResult)
    when(templateBodyRenderContextFactory(templateTraversalResult)).thenReturn(TheTemplateBodyRenderContext)
  }

  test("apply() when input has all Java-specific attributes") {
    val expectedCaseClassRenderContext = CaseClassRenderContext(
      javaModifiers = TheJavaModifiers,
      maybeInheritanceKeyword = Some(TheJavaInheritanceKeyword),
      bodyContext = TheTemplateBodyRenderContext
    )

    when(caseClassTraversalResult.javaModifiers).thenReturn(TheJavaModifiers)
    when(caseClassTraversalResult.maybeInheritanceKeyword).thenReturn(Some(TheJavaInheritanceKeyword))

    caseClassRenderContextFactory(caseClassTraversalResult) should equalCaseClassRenderContext(expectedCaseClassRenderContext)
  }

  test("apply() when input has no Java-specific attributes") {
    val expectedCaseClassRenderContext = CaseClassRenderContext(bodyContext = TheTemplateBodyRenderContext)

    when(caseClassTraversalResult.javaModifiers).thenReturn(Nil)
    when(caseClassTraversalResult.maybeInheritanceKeyword).thenReturn(None)

    caseClassRenderContextFactory(caseClassTraversalResult) should equalCaseClassRenderContext(expectedCaseClassRenderContext)
  }
}
