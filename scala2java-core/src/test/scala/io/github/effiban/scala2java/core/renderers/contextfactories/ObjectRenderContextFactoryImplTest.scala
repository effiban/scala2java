package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedObject, EnrichedTemplate}
import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}
import io.github.effiban.scala2java.core.renderers.contexts.{ObjectRenderContext, TemplateBodyRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.ObjectRenderContextScalatestMatcher.equalObjectRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{ObjectTraversalResult, TemplateTraversalResult}

import scala.meta.XtensionQuasiquoteTerm

class ObjectRenderContextFactoryImplTest extends UnitTestSuite {

  private val TheJavaModifiers = List(JavaModifier.Public)
  private val TheJavaTypeKeyword = JavaKeyword.Enum
  private val TheJavaInheritanceKeyword = JavaKeyword.Implements

  private val TheTemplateBodyRenderContext = TemplateBodyRenderContext(
    Map(
      q"final var x: Int = 3" -> VarRenderContext()
    )
  )

  @deprecated
  private val templateTraversalResult = mock[TemplateTraversalResult]
  @deprecated
  private val objectTraversalResult = mock[ObjectTraversalResult]

  private val enrichedTemplate = mock[EnrichedTemplate]
  private val enrichedObject = mock[EnrichedObject]

  private val templateBodyRenderContextFactory = mock[TemplateBodyRenderContextFactory]

  private val objectRenderContextFactory = new ObjectRenderContextFactoryImpl(templateBodyRenderContextFactory)

  override protected def beforeEach(): Unit = {
    when(objectTraversalResult.javaTypeKeyword).thenReturn(TheJavaTypeKeyword)
    when(objectTraversalResult.templateResult).thenReturn(templateTraversalResult)
    when(templateBodyRenderContextFactory(templateTraversalResult)).thenReturn(TheTemplateBodyRenderContext)

    when(enrichedObject.javaTypeKeyword).thenReturn(TheJavaTypeKeyword)
    when(enrichedObject.enrichedTemplate).thenReturn(enrichedTemplate)
    when(templateBodyRenderContextFactory(enrichedTemplate)).thenReturn(TheTemplateBodyRenderContext)
  }

  test("apply() to ObjectTraversalResult when input has all Java-specific attributes") {
    val expectedObjectRenderContext = ObjectRenderContext(
      javaModifiers = TheJavaModifiers,
      javaTypeKeyword = TheJavaTypeKeyword,
      maybeInheritanceKeyword = Some(TheJavaInheritanceKeyword),
      bodyContext = TheTemplateBodyRenderContext
    )

    when(objectTraversalResult.javaModifiers).thenReturn(TheJavaModifiers)
    when(objectTraversalResult.maybeInheritanceKeyword).thenReturn(Some(TheJavaInheritanceKeyword))

    objectRenderContextFactory(objectTraversalResult) should equalObjectRenderContext(expectedObjectRenderContext)
  }

  test("apply() to EnrichedObject when input has all Java-specific attributes") {
    val expectedObjectRenderContext = ObjectRenderContext(
      javaModifiers = TheJavaModifiers,
      javaTypeKeyword = TheJavaTypeKeyword,
      maybeInheritanceKeyword = Some(TheJavaInheritanceKeyword),
      bodyContext = TheTemplateBodyRenderContext
    )

    when(enrichedObject.javaModifiers).thenReturn(TheJavaModifiers)
    when(enrichedObject.maybeInheritanceKeyword).thenReturn(Some(TheJavaInheritanceKeyword))

    objectRenderContextFactory(enrichedObject) should equalObjectRenderContext(expectedObjectRenderContext)
  }

  test("apply() to ObjectTraversalResult when input has mandatory Java-specific attributes only") {
    val expectedObjectRenderContext = ObjectRenderContext(
      javaTypeKeyword = TheJavaTypeKeyword,
      bodyContext = TheTemplateBodyRenderContext
    )

    when(objectTraversalResult.javaModifiers).thenReturn(Nil)
    when(objectTraversalResult.maybeInheritanceKeyword).thenReturn(None)

    objectRenderContextFactory(objectTraversalResult) should equalObjectRenderContext(expectedObjectRenderContext)
  }

  test("apply() to EnrichedObject when input has mandatory Java-specific attributes only") {
    val expectedObjectRenderContext = ObjectRenderContext(
      javaTypeKeyword = TheJavaTypeKeyword,
      bodyContext = TheTemplateBodyRenderContext
    )

    when(enrichedObject.javaModifiers).thenReturn(Nil)
    when(enrichedObject.maybeInheritanceKeyword).thenReturn(None)

    objectRenderContextFactory(enrichedObject) should equalObjectRenderContext(expectedObjectRenderContext)
  }
}
