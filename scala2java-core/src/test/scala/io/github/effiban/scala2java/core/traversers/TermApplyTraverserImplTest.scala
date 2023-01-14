package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArrayInitializerValuesContext, DefnDefContext, InvocationArgListContext}
import io.github.effiban.scala2java.core.matchers.ArrayInitializerValuesContextMockitoMatcher.eqArrayInitializerValuesContext
import io.github.effiban.scala2java.core.matchers.DefnDefContextMatcher.eqDefnDefContext
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerContextResolver
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{TermNames, TypeNames}
import io.github.effiban.scala2java.spi.transformers.{TermApplyToDefnDefTransformer, TermApplyTransformer}
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.{Lit, Term, XtensionQuasiquoteTerm}

class TermApplyTraverserImplTest extends UnitTestSuite {
  private val termTraverser = mock[TermTraverser]
  private val arrayInitializerTraverser = mock[ArrayInitializerTraverser]
  private val invocationArgListTraverser = mock[InvocationArgListTraverser]
  private val defnDefTraverser = mock[DefnDefTraverser]
  private val arrayInitializerContextResolver = mock[ArrayInitializerContextResolver]
  private val termApplyToDefnDefTransformer = mock[TermApplyToDefnDefTransformer]
  private val termApplyTransformer = mock[TermApplyTransformer]

  private val termApplyTraverser = new TermApplyTraverserImpl(
    termTraverser,
    arrayInitializerTraverser,
    invocationArgListTraverser,
    defnDefTraverser,
    arrayInitializerContextResolver,
    termApplyToDefnDefTransformer,
    termApplyTransformer
  )

  test("traverse() a regular method invocation") {
    val scalaTermApply = Term.Apply(
      fun = Term.Name("myMethod"),
      args = List(Term.Name("arg1"), Term.Name("arg2"))
    )
    val javaTermApply = Term.Apply(
      fun = Term.Name("myJavaMethod"),
      args = List(Term.Name("arg1"), Term.Name("arg2"))
    )

    when(arrayInitializerContextResolver.tryResolve(eqTree(scalaTermApply))).thenReturn(None)
    when(termApplyToDefnDefTransformer.transform(eqTree(scalaTermApply))).thenReturn(None)
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

  test("traverse() a method invocation that is transformed into a method definition") {
    val termApply =
      q"""
      test("myTest") {
        checkSomething()
      }
      """

    val defnDef =
      q"""
      @Test
      def myTest(): Unit = {
        checkSomething()
      }
      """

    when(arrayInitializerContextResolver.tryResolve(eqTree(termApply))).thenReturn(None)
    when(termApplyToDefnDefTransformer.transform(eqTree(termApply))).thenReturn(Some(defnDef))
    doWrite(
      """
        |@Test
        |public void myTest() {
        |    checkSomething();
        |}
        |""".stripMargin)
      .when(defnDefTraverser).traverse(eqTree(defnDef), eqDefnDefContext(DefnDefContext()))

    termApplyTraverser.traverse(termApply)

    outputWriter.toString shouldBe
      """
        |@Test
        |public void myTest() {
        |    checkSomething();
        |}
        |""".stripMargin
  }
}
