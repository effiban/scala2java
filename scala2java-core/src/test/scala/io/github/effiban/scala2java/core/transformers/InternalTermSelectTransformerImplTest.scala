package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.matchers.TermSelectTransformationContextMatcher.eqTermSelectTransformationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.QualifierTypeInferrer
import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext
import io.github.effiban.scala2java.spi.transformers.{TermSelectNameTransformer, TermSelectTransformer}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class InternalTermSelectTransformerImplTest extends UnitTestSuite {

  private val MyClass = q"MyClass"
  private val MyType = t"MyType"
  private val MyMethod = q"myMethod"
  private val MyJavaClass = q"MyJavaClass"
  private val MyJavaMethod = q"myJavaMethod"
  private val MyScalaSelect = Term.Select(qual = MyClass, name = MyMethod)
  private val MyJavaSelect = Term.Select(qual = MyJavaClass, name = MyJavaMethod)

  private val treeTransformer = mock[TreeTransformer]
  private val termSelectTransformer = mock[TermSelectTransformer]
  private val termSelectNameTransformer = mock[TermSelectNameTransformer]
  private val qualifierTypeInferrer = mock[QualifierTypeInferrer]

  private val internalTermSelectTransformer = new InternalTermSelectTransformerImpl(
    treeTransformer,
    termSelectTransformer,
    termSelectNameTransformer,
    qualifierTypeInferrer
  )

  test("transform() when TermSelectTransformer returns a value should return it") {
    when(termSelectTransformer.transform(eqTree(MyScalaSelect))).thenReturn(Some(MyJavaSelect))

    internalTermSelectTransformer.transform(MyScalaSelect).structure shouldBe MyJavaSelect.structure
  }

  test("transform() when TermSelectTransformer returns None and has an inferred qualifier type") {
    val expectedTransformationContext = TermSelectTransformationContext(Some(MyType))

    when(termSelectTransformer.transform(eqTree(MyScalaSelect))).thenReturn(None)
    when(qualifierTypeInferrer.infer(eqTree(MyScalaSelect))).thenReturn(Some(MyType))
    when(termSelectNameTransformer.transform(eqTree(MyMethod), eqTermSelectTransformationContext(expectedTransformationContext)))
      .thenReturn(MyJavaMethod)

    doReturn(MyJavaClass).when(treeTransformer).transform(eqTree(MyClass))

    internalTermSelectTransformer.transform(MyScalaSelect).structure shouldBe MyJavaSelect.structure
  }

  test("traverse() when TermSelectTransformer returns None and has no inferred qualifier type") {
    val expectedTransformationContext = TermSelectTransformationContext()

    when(termSelectTransformer.transform(eqTree(MyScalaSelect))).thenReturn(None)
    when(qualifierTypeInferrer.infer(eqTree(MyScalaSelect))).thenReturn(None)
    when(termSelectNameTransformer.transform(eqTree(MyMethod), eqTermSelectTransformationContext(expectedTransformationContext)))
      .thenReturn(MyJavaMethod)

    doReturn(MyJavaClass).when(treeTransformer).transform(eqTree(MyClass))

    internalTermSelectTransformer.transform(MyScalaSelect).structure shouldBe MyJavaSelect.structure
  }
}
