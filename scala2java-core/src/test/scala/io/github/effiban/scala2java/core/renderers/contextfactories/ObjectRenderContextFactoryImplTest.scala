package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}
import io.github.effiban.scala2java.core.renderers.contexts.{ObjectRenderContext, TemplateBodyRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.ObjectRenderContextScalatestMatcher.equalObjectRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{ObjectTraversalResult, TemplateTraversalResult}

import scala.meta.XtensionQuasiquoteTerm

class ObjectRenderContextFactoryImplTest extends UnitTestSuite {

  private val TheJavaModifiers = List(JavaModifier.Public)
  private val TheJavaTypeKeyword = JavaKeyword.Class
  private val TheJavaInheritanceKeyword = JavaKeyword.Implements

  private val TheTemplateBodyRenderContext = TemplateBodyRenderContext(
    Map(
      q"final var x: Int = 3" -> VarRenderContext()
    )
  )

  private val templateTraversalResult = mock[TemplateTraversalResult]
  private val objectTraversalResult = mock[ObjectTraversalResult]

  private val templateBodyRenderContextFactory = mock[TemplateBodyRenderContextFactory]

  private val objectRenderContextFactory = new ObjectRenderContextFactoryImpl(templateBodyRenderContextFactory)

  override protected def beforeEach(): Unit = {
    when(objectTraversalResult.templateResult).thenReturn(templateTraversalResult)
    when(templateBodyRenderContextFactory(templateTraversalResult)).thenReturn(TheTemplateBodyRenderContext)
    when(objectTraversalResult.javaTypeKeyword).thenReturn(TheJavaTypeKeyword)
  }

  test("apply() when input has all Java-specific attributes") {
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

  test("apply() when input has mandatory Java-specific attributes only") {
    val expectedTraitRenderContext = ObjectRenderContext(
      javaTypeKeyword = TheJavaTypeKeyword,
      bodyContext = TheTemplateBodyRenderContext
    )

    when(objectTraversalResult.javaModifiers).thenReturn(Nil)
    when(objectTraversalResult.maybeInheritanceKeyword).thenReturn(None)

    objectRenderContextFactory(objectTraversalResult) should equalObjectRenderContext(expectedTraitRenderContext)
  }
}
