package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ArgumentListContext, ArrayInitializerValuesRenderContext}
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.matchers.ArrayInitializerValuesRenderContextMockitoMatcher.eqArrayInitializerValuesRenderContext
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerRenderContextResolver
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{TermNames, TypeNames}
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any
import org.mockito.captor.ArgCaptor

import scala.meta.{Lit, Term}

class TermApplyRendererImplTest extends UnitTestSuite {
  private val expressionTermRenderer = mock[ExpressionTermRenderer]
  private val arrayInitializerRenderer = mock[ArrayInitializerRenderer]
  private val argListRenderer = mock[ArgumentListRenderer]
  private val invocationArgRenderer = mock[InvocationArgRenderer[Term]]
  private val arrayInitializerRenderContextResolver = mock[ArrayInitializerRenderContextResolver]

  private val argRendererProviderCaptor = ArgCaptor[Int => ArgumentRenderer[Term]]

  private val termApplyRenderer = new TermApplyRendererImpl(
    expressionTermRenderer,
    arrayInitializerRenderer,
    argListRenderer,
    invocationArgRenderer,
    arrayInitializerRenderContextResolver
  )

  test("render() a regular method invocation") {
    val termApply = Term.Apply(
      fun = Term.Name("myMethod"),
      args = List(Term.Name("arg1"), Term.Name("arg2"))
    )

    val expectedArgListContext = ArgumentListContext(
      options = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses), traverseEmpty = true),
      argNameAsComment = true
    )

    when(arrayInitializerRenderContextResolver.tryResolve(eqTree(termApply))).thenReturn(None)

    doWrite("myMethod").when(expressionTermRenderer).render(eqTree(termApply.fun))
    doWrite("(arg1, arg2)").when(argListRenderer).render(
      args = eqTreeList(termApply.args),
      any[Int => ArgumentRenderer[Term]],
      context = eqArgumentListContext(expectedArgListContext)
    )

    termApplyRenderer.render(termApply)

    outputWriter.toString shouldBe "myMethod(arg1, arg2)"

    verify(argListRenderer).render(
      args = eqTreeList(termApply.args),
      argRendererProviderCaptor.capture,
      context = eqArgumentListContext(expectedArgListContext)
    )

    argRendererProviderCaptor.value(0) shouldBe invocationArgRenderer
  }

  test("render() an Array initializer") {
    val values = List(Lit.String("a"), Lit.String("b"))
    val termApply = Term.Apply(
      fun = Term.ApplyType(TermNames.ScalaArray, List(TypeNames.String)),
      args = values
    )

    val expectedContext = ArrayInitializerValuesRenderContext(tpe = TypeNames.String, values = values)

    when(arrayInitializerRenderContextResolver.tryResolve(eqTree(termApply))).thenReturn(Some(expectedContext))

    termApplyRenderer.render(termApply)

    verify(arrayInitializerRenderer).renderWithValues(eqArrayInitializerValuesRenderContext(expectedContext))
  }

}
