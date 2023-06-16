package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArrayInitializerTypedValuesContext, ArrayInitializerValuesContext}
import io.github.effiban.scala2java.core.factories.TermApplyTransformationContextFactory
import io.github.effiban.scala2java.core.matchers.ArrayInitializerValuesContextMockitoMatcher.eqArrayInitializerValuesContext
import io.github.effiban.scala2java.core.matchers.TermApplyTransformationContextMockitoMatcher.eqTermApplyTransformationContext
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerContextResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames
import io.github.effiban.scala2java.core.testtrees.TermNames.ScalaArray
import io.github.effiban.scala2java.core.transformers.InternalTermApplyTransformer
import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TermApplyTraverserImplTest extends UnitTestSuite {
  private val expressionTermTraverser = mock[ExpressionTermTraverser]
  private val arrayInitializerTraverser = mock[ArrayInitializerTraverser]
  private val termApplyTransformationContextFactory = mock[TermApplyTransformationContextFactory]
  private val arrayInitializerContextResolver = mock[ArrayInitializerContextResolver]
  private val termApplyTransformer = mock[InternalTermApplyTransformer]

  private val termApplyTraverser = new TermApplyTraverserImpl(
    expressionTermTraverser,
    arrayInitializerTraverser,
    termApplyTransformationContextFactory,
    arrayInitializerContextResolver,
    termApplyTransformer
  )

  test("traverse() a regular method invocation") {
    val fun = q"myMethod"
    val arg1 = q"arg1"
    val arg2 = q"arg2"
    val termApply = Term.Apply(fun, List(arg1, arg2))

    val transformedFun = q"myTransformedMethod"
    val transformedArg1 = q"transformedArg1"
    val transformedArg2 = q"transformedArg2"
    val transformedTermApply = Term.Apply(transformedFun, List(transformedArg1, transformedArg2))

    val traversedFun = q"myTraversedMethod"
    val traversedArg1 = q"traversedArg1"
    val traversedArg2 = q"traversedArg2"
    val traversedTermApply = Term.Apply(traversedFun, List(traversedArg1, traversedArg2))

    val expectedTransformationContext = TermApplyTransformationContext(maybeParentType = Some(t"MyParent"))

    when(arrayInitializerContextResolver.tryResolve(eqTree(termApply))).thenReturn(None)
    when(termApplyTransformationContextFactory.create(eqTree(termApply))).thenReturn(expectedTransformationContext)
    when(termApplyTransformer.transform(eqTree(termApply), eqTermApplyTransformationContext(expectedTransformationContext)))
      .thenReturn(transformedTermApply)

    doAnswer((arg: Term) => arg match {
      case anArg if anArg.structure == transformedFun.structure => traversedFun
      case anArg if anArg.structure == transformedArg1.structure => traversedArg1
      case anArg if anArg.structure == transformedArg2.structure => traversedArg2
      case anArg => anArg
    }).when(expressionTermTraverser).traverse(any[Term])

    termApplyTraverser.traverse(termApply).structure shouldBe traversedTermApply.structure
  }

  test("traverse() an Array initializer when 'fun' is 'Array'") {
    val expectedOutputType = t"MyOutputType"

    val inputValues = List(q"in1", q"in2")
    val expectedOutputValues = List(q"out1", q"out2")

    val inputTermApply = Term.Apply(ScalaArray, inputValues)

    val expectedOutputTermApply = Term.Apply(
      fun = Term.ApplyType(ScalaArray, List(expectedOutputType)),
      args = expectedOutputValues
    )

    val expectedInputContext = ArrayInitializerValuesContext(values = inputValues)
    val expectedOutputContext = ArrayInitializerTypedValuesContext(tpe = expectedOutputType, values = expectedOutputValues)

    when(arrayInitializerContextResolver.tryResolve(eqTree(inputTermApply))).thenReturn(Some(expectedInputContext))
    when(arrayInitializerTraverser.traverseWithValues(eqArrayInitializerValuesContext(expectedInputContext)))
      .thenReturn(expectedOutputContext)

    termApplyTraverser.traverse(inputTermApply).structure shouldBe expectedOutputTermApply.structure
  }

  test("traverse() an Array initializer when 'fun' is 'Array.apply'") {
    val expectedOutputType = t"MyOutputType"

    val inputValues = List(q"in1", q"in2")
    val expectedOutputValues = List(q"out1", q"out2")

    val inputTermApply = Term.Apply(Term.Select(ScalaArray, TermNames.Apply), inputValues)

    val expectedOutputTermApply = Term.Apply(
      fun = Term.ApplyType(Term.Select(ScalaArray, TermNames.Apply), List(expectedOutputType)),
      args = expectedOutputValues
    )

    val expectedInputContext = ArrayInitializerValuesContext(values = inputValues)
    val expectedOutputContext = ArrayInitializerTypedValuesContext(tpe = expectedOutputType, values = expectedOutputValues)

    when(arrayInitializerContextResolver.tryResolve(eqTree(inputTermApply))).thenReturn(Some(expectedInputContext))
    when(arrayInitializerTraverser.traverseWithValues(eqArrayInitializerValuesContext(expectedInputContext)))
      .thenReturn(expectedOutputContext)

    termApplyTraverser.traverse(inputTermApply).structure shouldBe expectedOutputTermApply.structure
  }

  test("traverse() an Array initializer when 'fun' is 'Array[MyType]'") {
    val inputType = t"MyType"
    val expectedOutputType = t"MyOutputType"

    val inputValues = List(q"in1", q"in2")
    val expectedOutputValues = List(q"out1", q"out2")

    val inputTermApply = Term.Apply(
      fun = Term.ApplyType(ScalaArray, List(inputType)),
      args = inputValues
    )

    val expectedOutputTermApply = Term.Apply(
      fun = Term.ApplyType(ScalaArray, List(expectedOutputType)),
      args = expectedOutputValues
    )

    val expectedInputContext = ArrayInitializerValuesContext(maybeType = Some(inputType), values = inputValues)
    val expectedOutputContext = ArrayInitializerTypedValuesContext(tpe = expectedOutputType, values = expectedOutputValues)

    when(arrayInitializerContextResolver.tryResolve(eqTree(inputTermApply))).thenReturn(Some(expectedInputContext))
    when(arrayInitializerTraverser.traverseWithValues(eqArrayInitializerValuesContext(expectedInputContext)))
      .thenReturn(expectedOutputContext)

    termApplyTraverser.traverse(inputTermApply).structure shouldBe expectedOutputTermApply.structure
  }
}
