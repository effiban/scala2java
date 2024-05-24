package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArrayInitializerTypedValuesContext, ArrayInitializerValuesContext}
import io.github.effiban.scala2java.core.entities.TermNames
import io.github.effiban.scala2java.core.entities.TermSelects.ScalaArray
import io.github.effiban.scala2java.core.factories.UnqualifiedTermApplyTransformationContextFactory
import io.github.effiban.scala2java.core.matchers.ArrayInitializerValuesContextMockitoMatcher.eqArrayInitializerValuesContext
import io.github.effiban.scala2java.core.matchers.UnqualifiedTermApplyTransformationContextMockitoMatcher.eqUnqualifiedTermApplyTransformationContext
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerContextResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.InternalTermApplyTransformer
import io.github.effiban.scala2java.spi.contexts.UnqualifiedTermApplyTransformationContext
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TermApplyTraverserImplTest extends UnitTestSuite {

  private val fun = Term.Name("foo")
  private val transformedFun = Term.Name("transformedFoo")
  private val traversedFun = Term.Name("traversedFoo")

  private val arg1 = Term.Name("arg1")
  private val arg2 = Term.Name("arg2")
  private val arg3 = Term.Name("arg3")
  private val arg4 = Term.Name("arg4")
  private val arg5 = Term.Name("arg5")
  private val arg6 = Term.Name("arg6")

  private val transformedArg1 = Term.Name("transformedArg1")
  private val transformedArg2 = Term.Name("transformedArg2")
  private val transformedArg3 = Term.Name("transformedArg3")
  private val transformedArg4 = Term.Name("transformedArg4")
  private val transformedArg5 = Term.Name("transformedArg5")
  private val transformedArg6 = Term.Name("transformedArg6")

  private val traversedArg1 = Term.Name("traversedArg1")
  private val traversedArg2 = Term.Name("traversedArg2")
  private val traversedArg3 = Term.Name("traversedArg3")
  private val traversedArg4 = Term.Name("traversedArg4")
  private val traversedArg5 = Term.Name("traversedArg5")
  private val traversedArg6 = Term.Name("traversedArg6")

  private val expressionTermTraverser = mock[ExpressionTermTraverser]
  private val arrayInitializerTraverser = mock[ArrayInitializerTraverser]
  private val unqualifiedTermApplyTransformationContextFactory = mock[UnqualifiedTermApplyTransformationContextFactory]
  private val arrayInitializerContextResolver = mock[ArrayInitializerContextResolver]
  private val termApplyTransformer = mock[InternalTermApplyTransformer]

  private val termApplyTraverser = new TermApplyTraverserImpl(
    expressionTermTraverser,
    arrayInitializerTraverser,
    unqualifiedTermApplyTransformationContextFactory,
    arrayInitializerContextResolver,
    termApplyTransformer
  )

  test("traverse() a regular uncurried method invocation") {
    val termApply = Term.Apply(fun, List(arg1, arg2))
    val transformedTermApply = Term.Apply(transformedFun, List(transformedArg1, transformedArg2))
    val traversedTermApply = Term.Apply(traversedFun, List(traversedArg1, traversedArg2))

    val expectedTransformationContext = UnqualifiedTermApplyTransformationContext(maybeQualifierType = Some(t"MyParent"))

    when(arrayInitializerContextResolver.tryResolve(eqTree(termApply))).thenReturn(None)
    when(unqualifiedTermApplyTransformationContextFactory.create(eqTree(termApply))).thenReturn(expectedTransformationContext)
    when(termApplyTransformer.transform(eqTree(termApply), eqUnqualifiedTermApplyTransformationContext(expectedTransformationContext)))
      .thenReturn(transformedTermApply)

    doAnswer((arg: Term) => arg match {
      case anArg if anArg.structure == transformedFun.structure => traversedFun
      case anArg if anArg.structure == transformedArg1.structure => traversedArg1
      case anArg if anArg.structure == transformedArg2.structure => traversedArg2
      case anArg => anArg
    }).when(expressionTermTraverser).traverse(any[Term])

    termApplyTraverser.traverse(termApply).structure shouldBe traversedTermApply.structure
  }

  test("transform() of a 2-level curried invocation, should convert to a single invocation with concatenated args") {
    val curriedTermApply =
      Term.Apply(
        Term.Apply(fun, List(arg1, arg2)),
        List(arg3, arg4)
      )
    val flattenedTermApply = Term.Apply(fun, List(arg1, arg2, arg3, arg4))
    val transformedTermApply = Term.Apply(transformedFun, List(transformedArg1, transformedArg2, transformedArg3, transformedArg4))
    val traversedTermApply = Term.Apply(traversedFun, List(traversedArg1, traversedArg2, traversedArg3, traversedArg4))

    val expectedTransformationContext = UnqualifiedTermApplyTransformationContext(maybeQualifierType = Some(t"MyParent"))

    when(arrayInitializerContextResolver.tryResolve(eqTree(flattenedTermApply))).thenReturn(None)
    when(unqualifiedTermApplyTransformationContextFactory.create(eqTree(flattenedTermApply))).thenReturn(expectedTransformationContext)
    when(termApplyTransformer.transform(eqTree(flattenedTermApply), eqUnqualifiedTermApplyTransformationContext(expectedTransformationContext)))
      .thenReturn(transformedTermApply)

    doAnswer((arg: Term) => arg match {
      case anArg if anArg.structure == transformedFun.structure => traversedFun
      case anArg if anArg.structure == transformedArg1.structure => traversedArg1
      case anArg if anArg.structure == transformedArg2.structure => traversedArg2
      case anArg if anArg.structure == transformedArg3.structure => traversedArg3
      case anArg if anArg.structure == transformedArg4.structure => traversedArg4
      case anArg => anArg
    }).when(expressionTermTraverser).traverse(any[Term])

    termApplyTraverser.traverse(curriedTermApply).structure shouldBe traversedTermApply.structure
  }

  test("transform() of a 3-level curried invocation, should convert to a single invocation with concatenated args") {
    val curriedTermApply =
      Term.Apply(
        Term.Apply(
          Term.Apply(fun, List(arg1, arg2)),
          List(arg3, arg4)
        ),
        List(arg5, arg6)
      )

    val flattenedTermApply = Term.Apply(fun, List(arg1, arg2, arg3, arg4, arg5, arg6))
    val transformedTermApply = Term.Apply(
      transformedFun,
      List(transformedArg1, transformedArg2, transformedArg3, transformedArg4, transformedArg5, transformedArg6)
    )
    val traversedTermApply = Term.Apply(
      traversedFun,
      List(traversedArg1, traversedArg2, traversedArg3, traversedArg4, traversedArg5, traversedArg6)
    )

    val expectedTransformationContext = UnqualifiedTermApplyTransformationContext(maybeQualifierType = Some(t"MyParent"))

    when(arrayInitializerContextResolver.tryResolve(eqTree(flattenedTermApply))).thenReturn(None)
    when(unqualifiedTermApplyTransformationContextFactory.create(eqTree(flattenedTermApply))).thenReturn(expectedTransformationContext)
    when(termApplyTransformer.transform(eqTree(flattenedTermApply), eqUnqualifiedTermApplyTransformationContext(expectedTransformationContext)))
      .thenReturn(transformedTermApply)

    doAnswer((arg: Term) => arg match {
      case anArg if anArg.structure == transformedFun.structure => traversedFun
      case anArg if anArg.structure == transformedArg1.structure => traversedArg1
      case anArg if anArg.structure == transformedArg2.structure => traversedArg2
      case anArg if anArg.structure == transformedArg3.structure => traversedArg3
      case anArg if anArg.structure == transformedArg4.structure => traversedArg4
      case anArg if anArg.structure == transformedArg5.structure => traversedArg5
      case anArg if anArg.structure == transformedArg6.structure => traversedArg6
      case anArg => anArg
    }).when(expressionTermTraverser).traverse(any[Term])

    termApplyTraverser.traverse(curriedTermApply).structure shouldBe traversedTermApply.structure
  }

  test("traverse() an Array initializer when 'fun' is 'scala.Array'") {
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

  test("traverse() an Array initializer when 'fun' is 'scala.Array.apply'") {
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

  test("traverse() an Array initializer when 'fun' is 'scala.Array[MyType]'") {
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
