package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentListContext, ArrayInitializerValuesContext}
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.factories.TermApplyTransformationContextFactory
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.matchers.ArrayInitializerValuesContextMockitoMatcher.eqArrayInitializerValuesContext
import io.github.effiban.scala2java.core.matchers.TermApplyTransformationContextMockitoMatcher.eqTermApplyTransformationContext
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerContextResolver
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{TermNames, TypeNames}
import io.github.effiban.scala2java.core.transformers.InternalTermApplyTransformer
import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Lit, Term, XtensionQuasiquoteType}

class TermApplyTraverserImplTest extends UnitTestSuite {
  private val termApplyFunTraverser = mock[TermTraverser]
  private val arrayInitializerTraverser = mock[ArrayInitializerTraverser]
  private val argListTraverser = mock[ArgumentListTraverser]
  private val invocationArgTraverser = mock[ArgumentTraverser[Term]]
  private val termApplyTransformationContextFactory = mock[TermApplyTransformationContextFactory]
  private val arrayInitializerContextResolver = mock[ArrayInitializerContextResolver]
  private val termApplyTransformer = mock[InternalTermApplyTransformer]

  private val termApplyTraverser = new TermApplyTraverserImpl(
    termApplyFunTraverser,
    arrayInitializerTraverser,
    argListTraverser,
    invocationArgTraverser,
    termApplyTransformationContextFactory,
    arrayInitializerContextResolver,
    termApplyTransformer
  )

  test("traverse() a regular method invocation") {
    val termApply = Term.Apply(
      fun = Term.Name("myMethod"),
      args = List(Term.Name("arg1"), Term.Name("arg2"))
    )
    val transformedTermApply = Term.Apply(
      fun = Term.Name("myTransformedMethod"),
      args = List(Term.Name("transformedArg1"), Term.Name("transformedArg2"))
    )

    val expectedTransformationContext = TermApplyTransformationContext(maybeParentType = Some(t"MyParent"))

    val expectedArgListContext = ArgumentListContext(
      options = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses), traverseEmpty = true),
      argNameAsComment = true
    )

    when(arrayInitializerContextResolver.tryResolve(eqTree(termApply))).thenReturn(None)
    when(termApplyTransformationContextFactory.create(termApply)).thenReturn(expectedTransformationContext)
    when(termApplyTransformer.transform(eqTree(termApply), eqTermApplyTransformationContext(expectedTransformationContext))).thenReturn(transformedTermApply)

    doWrite("myTransformedMethod").when(termApplyFunTraverser).traverse(eqTree(transformedTermApply.fun))
    doWrite("(transformedArg1, transformedArg2)").when(argListTraverser).traverse(
      args = eqTreeList(transformedTermApply.args),
      argTraverser = eqTo(invocationArgTraverser),
      context = eqArgumentListContext(expectedArgListContext)
    )

    termApplyTraverser.traverse(termApply)

    outputWriter.toString shouldBe "myTransformedMethod(transformedArg1, transformedArg2)"

  }

  test("traverse() an Array initializer") {
    val values = List(Lit.String("a"), Lit.String("b"))
    val termApply = Term.Apply(
      fun = Term.ApplyType(TermNames.ScalaArray, List(TypeNames.String)),
      args = values
    )

    val expectedContext = ArrayInitializerValuesContext(maybeType = Some(TypeNames.String), values = values)

    when(arrayInitializerContextResolver.tryResolve(eqTree(termApply))).thenReturn(Some(expectedContext))

    termApplyTraverser.traverse(termApply)

    verify(arrayInitializerTraverser).traverseWithValues(eqArrayInitializerValuesContext(expectedContext))
  }
}
