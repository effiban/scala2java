package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.matchers.TermSelectTransformationContextMatcher.eqTermSelectTransformationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.QualifierTypeInferrer
import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext
import io.github.effiban.scala2java.spi.transformers.{TermSelectNameTransformer, TermSelectTransformer}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ExpressionTermSelectTraverserImplTest extends UnitTestSuite {

  private val MyInstance = q"MyObject"
  private val MyTraversedInstance = q"MyTraversedObject"
  private val MyType = t"MyType"
  private val MyMethod = Term.Name("myMethod")
  private val MyJavaClass = Term.Name("MyJavaClass")
  private val MyTraversedJavaClass = Term.Name("MyTraversedJavaClass")
  private val MyJavaMethod = Term.Name("myJavaMethod")
  private val MyScalaSelect = Term.Select(qual = MyInstance, name = MyMethod)
  private val MyJavaSelect = Term.Select(qual = MyJavaClass, name = MyJavaMethod)
  private val MyTraversedScalaSelect = Term.Select(qual = MyTraversedInstance, name = MyMethod)
  private val MyTraversedJavaSelect = Term.Select(qual = MyTraversedJavaClass, name = MyJavaMethod)

  private val expressionTermTraverser = mock[ExpressionTermTraverser]
  private val qualifierTypeInferrer = mock[QualifierTypeInferrer]
  private val termSelectTransformer = mock[TermSelectTransformer]
  private val termSelectNameTransformer = mock[TermSelectNameTransformer]

  private val expressionTermSelectTraverser = new ExpressionTermSelectTraverserImpl(
    expressionTermTraverser,
    qualifierTypeInferrer,
    termSelectTransformer,
    termSelectNameTransformer
  )

  test("traverse() when qualifier is a Term.Name and TermSelectTransformer returns a Term.Select") {
    when(qualifierTypeInferrer.infer(eqTree(MyScalaSelect))).thenReturn(Some(MyType))
    when(termSelectTransformer.transform(eqTree(MyScalaSelect))).thenReturn(Some(MyJavaSelect))

    doReturn(MyTraversedJavaClass).when(expressionTermTraverser).traverse(eqTree(MyJavaClass))

    expressionTermSelectTraverser.traverse(MyScalaSelect).structure shouldBe MyTraversedJavaSelect.structure
  }

  test("traverse() when qualifier is a Term.Name and TermSelectTransformer returns a term which is not a Term.Select") {
    val expectedTerm = q"foo(123)"
    val expectedTraversedTerm = q"traversedFoo(123)"

    when(termSelectTransformer.transform(eqTree(MyScalaSelect))).thenReturn(Some(expectedTerm))

    doReturn(expectedTraversedTerm).when(expressionTermTraverser).traverse(eqTree(expectedTerm))

    expressionTermSelectTraverser.traverse(MyScalaSelect).structure shouldBe expectedTraversedTerm.structure
  }

  test("traverse() when qualifier is a Term.Name, TermSelectTransformer returns None, and has an inferred qualifier type") {
    val expectedTransformationContext = TermSelectTransformationContext(Some(MyType))

    when(termSelectTransformer.transform(eqTree(MyScalaSelect))).thenReturn(None)
    when(qualifierTypeInferrer.infer(eqTree(MyScalaSelect))).thenReturn(Some(MyType))
    when(termSelectNameTransformer.transform(eqTree(MyMethod), eqTermSelectTransformationContext(expectedTransformationContext)))
      .thenReturn(MyJavaMethod)

    doReturn(MyTraversedInstance).when(expressionTermTraverser).traverse(eqTree(MyInstance))

    expressionTermSelectTraverser.traverse(MyScalaSelect).structure shouldBe Term.Select(MyTraversedInstance, MyJavaMethod).structure
  }

  test("traverse() when qualifier is a Term.Name, TermSelectTransformer returns None, and has no inferred qualifier type") {
    val expectedTransformationContext = TermSelectTransformationContext()

    when(qualifierTypeInferrer.infer(eqTree(MyScalaSelect))).thenReturn(None)
    when(termSelectTransformer.transform(eqTree(MyScalaSelect))).thenReturn(None)
    when(termSelectNameTransformer.transform(eqTree(MyMethod), eqTermSelectTransformationContext(expectedTransformationContext)))
      .thenReturn(MyJavaMethod)

    doReturn(MyTraversedInstance).when(expressionTermTraverser).traverse(eqTree(MyInstance))

    expressionTermSelectTraverser.traverse(MyScalaSelect).structure shouldBe Term.Select(MyTraversedInstance, MyJavaMethod).structure
  }
}
