package io.github.effiban.scala2java.core.renderers.contextfactories

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

  private val templateTraversalResult = mock[TemplateTraversalResult]
  private val regularClassTraversalResult = mock[RegularClassTraversalResult]

  private val templateBodyRenderContextFactory = mock[TemplateBodyRenderContextFactory]

  private val regularClassRenderContextFactory = new RegularClassRenderContextFactoryImpl(templateBodyRenderContextFactory)

  override protected def beforeEach(): Unit = {
    when(regularClassTraversalResult.javaTypeKeyword).thenReturn(TheJavaTypeKeyword)
    when(regularClassTraversalResult.templateResult).thenReturn(templateTraversalResult)
    when(templateBodyRenderContextFactory(templateTraversalResult)).thenReturn(TheTemplateBodyRenderContext)
  }

  test("apply() when input has all Java-specific attributes") {
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

  test("apply() when input has mandatory Java-specific attributes only") {
    val expectedRegularClassRenderContext = RegularClassRenderContext(
      javaTypeKeyword = TheJavaTypeKeyword,
      bodyContext = TheTemplateBodyRenderContext
    )

    when(regularClassTraversalResult.javaModifiers).thenReturn(Nil)
    when(regularClassTraversalResult.maybeInheritanceKeyword).thenReturn(None)

    regularClassRenderContextFactory(regularClassTraversalResult) should equalRegularClassRenderContext(expectedRegularClassRenderContext)
  }
}
