package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{ArrayInitializerValuesContext, InvocationArgListContext}
import io.github.effiban.scala2java.matchers.ArrayInitializerValuesContextMockitoMatcher.eqArrayInitializerValuesContext
import io.github.effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.resolvers.ArrayInitializerContextResolver
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.{TermNames, TypeNames}
import io.github.effiban.scala2java.transformers.TermApplyTransformer
import org.mockito.ArgumentMatchers

import scala.meta.{Lit, Term}

class TermApplyTraverserImplTest extends UnitTestSuite {
  private val termTraverser = mock[TermTraverser]
  private val arrayInitializerTraverser = mock[ArrayInitializerTraverser]
  private val invocationArgListTraverser = mock[InvocationArgListTraverser]
  private val arrayInitializerContextResolver = mock[ArrayInitializerContextResolver]
  private val termApplyTransformer = mock[TermApplyTransformer]

  private val termApplyTraverser = new TermApplyTraverserImpl(
    termTraverser,
    arrayInitializerTraverser,
    invocationArgListTraverser,
    arrayInitializerContextResolver,
    termApplyTransformer
  )

  test("traverse() an arbitrary method invocation") {
    val scalaTermApply = Term.Apply(
      fun = Term.Name("myMethod"),
      args = List(Term.Name("arg1"), Term.Name("arg2"))
    )
    val javaTermApply = Term.Apply(
      fun = Term.Name("myJavaMethod"),
      args = List(Term.Name("arg1"), Term.Name("arg2"))
    )

    when(arrayInitializerContextResolver.tryResolve(eqTree(scalaTermApply))).thenReturn(None)
    when(termApplyTransformer.transform(eqTree(scalaTermApply))).thenReturn(javaTermApply)

    doWrite("myJavaMethod").when(termTraverser).traverse(eqTree(javaTermApply.fun))
    doWrite("(arg1, arg2)").when(invocationArgListTraverser).traverse(
      args = eqTreeList(javaTermApply.args),
      ArgumentMatchers.eq(InvocationArgListContext(traverseEmpty = true, argNameAsComment = true))
    )

    termApplyTraverser.traverse(scalaTermApply)

    outputWriter.toString shouldBe "myJavaMethod(arg1, arg2)"

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