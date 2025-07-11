package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TypeSelects
import io.github.effiban.scala2java.core.matchers.TermSelectInferenceContextMatcher.eqTermSelectInferenceContext
import io.github.effiban.scala2java.core.reflection.ScalaReflectionTypeInferrer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.spi.predicates.TermSelectSupportsNoArgInvocation
import io.github.effiban.scala2java.spi.typeinferrers.SelectTypeInferrer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class InternalSelectTypeInferrerImplTest extends UnitTestSuite {

  private val applyReturnTypeInferrer = mock[ApplyReturnTypeInferrer]
  private val qualifierTypeInferrer = mock[QualifierTypeInferrer]
  private val selectTypeInferrer = mock[SelectTypeInferrer]
  private val termSelectSupportsNoArgInvocation = mock[TermSelectSupportsNoArgInvocation]
  private val scalaReflectionTypeInferrer = mock[ScalaReflectionTypeInferrer]
  private val internalSelectTypeInferrer = new InternalSelectTypeInferrerImpl(
    applyReturnTypeInferrer,
    qualifierTypeInferrer,
    selectTypeInferrer,
    termSelectSupportsNoArgInvocation,
    scalaReflectionTypeInferrer
  )

  test("infer() when Term.Select supports a no-arg invocation, should infer as a no-arg Term.Apply and return that result") {
    val termSelect = q"a.b"
    val qualifierType = t"A"
    val expectedContext = TermSelectInferenceContext(Some(qualifierType))
    val expectedTermApply = q"a.b()"
    val expectedReturnType = TypeSelects.JavaString

    when(qualifierTypeInferrer.infer(eqTree(termSelect))).thenReturn(Some(qualifierType))
    when(termSelectSupportsNoArgInvocation(eqTree(termSelect), eqTermSelectInferenceContext(expectedContext))).thenReturn(true)
    when(applyReturnTypeInferrer.infer(eqTree(expectedTermApply))).thenReturn(Some(expectedReturnType))

    internalSelectTypeInferrer.infer(termSelect).value.structure shouldBe expectedReturnType.structure
  }

  test("infer() when Term.Select supports a no-arg invocation, should infer as a no-arg Term.Apply and return None if that returns None") {
    val termSelect = q"a.b"
    val qualifierType = t"A"
    val expectedContext = TermSelectInferenceContext(Some(qualifierType))
    val expectedTermApply = q"a.b()"

    when(qualifierTypeInferrer.infer(eqTree(termSelect))).thenReturn(Some(qualifierType))
    when(termSelectSupportsNoArgInvocation(eqTree(termSelect), eqTermSelectInferenceContext(expectedContext))).thenReturn(true)
    when(applyReturnTypeInferrer.infer(eqTree(expectedTermApply))).thenReturn(None)

    internalSelectTypeInferrer.infer(termSelect) shouldBe None
  }

  test("infer() when Term.Select does not support no-arg invocation, " +
    "and qualifier type is inferred, " +
    "and custom inferrer returns a type " +
    "- should return it") {
    val termSelect = q"a.b"
    val qualifierType = t"A"
    val expectedContext = TermSelectInferenceContext(Some(qualifierType))
    val expectedSelectType = TypeSelects.JavaString

    when(termSelectSupportsNoArgInvocation(eqTree(termSelect), eqTermSelectInferenceContext(expectedContext))).thenReturn(false)
    when(qualifierTypeInferrer.infer(eqTree(termSelect))).thenReturn(Some(qualifierType))
    when(selectTypeInferrer.infer(eqTree(termSelect), eqTermSelectInferenceContext(expectedContext))).thenReturn(Some(expectedSelectType))

    internalSelectTypeInferrer.infer(termSelect).value.structure shouldBe expectedSelectType.structure
  }

  test("infer() when Term.Select does not support no-arg invocation, " +
    "and qualifier type is inferred, " +
    "and custom inferrer returns None ," +
    "and reflection inferrer returns a type ," +
    "- should return that type") {
    val termSelect = q"a.b"
    val qualifierType = t"A"
    val expectedContext = TermSelectInferenceContext(Some(qualifierType))
    val expectedInferredType = TypeSelects.JavaString

    when(termSelectSupportsNoArgInvocation(eqTree(termSelect), eqTermSelectInferenceContext(expectedContext))).thenReturn(false)
    when(qualifierTypeInferrer.infer(eqTree(termSelect))).thenReturn(Some(qualifierType))
    when(selectTypeInferrer.infer(eqTree(termSelect), eqTermSelectInferenceContext(expectedContext))).thenReturn(None)
    when(scalaReflectionTypeInferrer.inferScalaMetaTypeOf(eqTree(qualifierType), eqTree(termSelect.name))).thenReturn(Some(expectedInferredType))

    internalSelectTypeInferrer.infer(termSelect).value.structure shouldBe expectedInferredType.structure
  }

  test("infer() when Term.Select does not support no-arg invocation, " +
    "and qualifier type is inferred, " +
    "and custom inferrer returns None ," +
    "and reflection inferrer returns None ," +
    "- should return None") {
    val termSelect = q"a.b"
    val qualifierType = t"A"
    val expectedContext = TermSelectInferenceContext(Some(qualifierType))

    when(termSelectSupportsNoArgInvocation(eqTree(termSelect), eqTermSelectInferenceContext(expectedContext))).thenReturn(false)
    when(qualifierTypeInferrer.infer(eqTree(termSelect))).thenReturn(Some(qualifierType))
    when(selectTypeInferrer.infer(eqTree(termSelect), eqTermSelectInferenceContext(expectedContext))).thenReturn(None)
    when(scalaReflectionTypeInferrer.inferScalaMetaTypeOf(eqTree(qualifierType), eqTree(termSelect.name))).thenReturn(None)

    internalSelectTypeInferrer.infer(termSelect) shouldBe None
  }

  test("infer() when Term.Select does not support no-arg invocation, " +
    "and qualifier type is not inferred, " +
    "and custom inferrer returns None ," +
    "and reflection inferrer returns a type ," +
    "- should return that type") {
    val termSelect = q"a.b"
    val expectedContext = TermSelectInferenceContext()
    val expectedInferredType = TypeSelects.JavaString

    when(termSelectSupportsNoArgInvocation(eqTree(termSelect), eqTermSelectInferenceContext(expectedContext))).thenReturn(false)
    when(qualifierTypeInferrer.infer(eqTree(termSelect))).thenReturn(None)
    when(selectTypeInferrer.infer(eqTree(termSelect), eqTermSelectInferenceContext(expectedContext))).thenReturn(None)
    when(scalaReflectionTypeInferrer.inferScalaMetaTypeOf(eqTree(q"a"), eqTree(termSelect.name))).thenReturn(Some(expectedInferredType))

    internalSelectTypeInferrer.infer(termSelect).value.structure shouldBe expectedInferredType.structure
  }

  test("infer() when Term.Select does not support no-arg invocation, " +
    "and qualifier type is not inferred, " +
    "and custom inferrer returns None ," +
    "and reflection inferrer returns None ," +
    "- should return None") {
    val termSelect = q"a.b"
    val expectedContext = TermSelectInferenceContext()

    when(termSelectSupportsNoArgInvocation(eqTree(termSelect), eqTermSelectInferenceContext(expectedContext))).thenReturn(false)
    when(qualifierTypeInferrer.infer(eqTree(termSelect))).thenReturn(None)
    when(selectTypeInferrer.infer(eqTree(termSelect), eqTermSelectInferenceContext(expectedContext))).thenReturn(None)
    when(scalaReflectionTypeInferrer.inferScalaMetaTypeOf(eqTree(q"a"), eqTree(termSelect.name))).thenReturn(None)

    internalSelectTypeInferrer.infer(termSelect) shouldBe None
  }

  test("infer() for first elem of a Tuple2 should return correct type") {
    val termSelect = q"""("a", 1)._1"""
    val qualifierType = t"(java.lang.String, scala.Int)"
    val expectedContext = TermSelectInferenceContext(Some(qualifierType))

    when(termSelectSupportsNoArgInvocation(eqTree(termSelect), eqTermSelectInferenceContext(expectedContext))).thenReturn(false)
    when(qualifierTypeInferrer.infer(eqTree(termSelect))).thenReturn(Some(qualifierType))
    when(selectTypeInferrer.infer(eqTree(termSelect), eqTermSelectInferenceContext(expectedContext))).thenReturn(None)

    internalSelectTypeInferrer.infer(termSelect).value.structure shouldBe TypeSelects.JavaString.structure
  }

  test("infer() for second elem of a Tuple2 should return correct type") {
    val termSelect = q"""("a", 1)._2"""
    val qualifierType = t"(java.lang.String, scala.Int)"
    val expectedContext = TermSelectInferenceContext(Some(qualifierType))

    when(termSelectSupportsNoArgInvocation(eqTree(termSelect), eqTermSelectInferenceContext(expectedContext))).thenReturn(false)
    when(qualifierTypeInferrer.infer(eqTree(termSelect))).thenReturn(Some(qualifierType))
    when(selectTypeInferrer.infer(eqTree(termSelect), eqTermSelectInferenceContext(expectedContext))).thenReturn(None)

    internalSelectTypeInferrer.infer(termSelect).value.structure shouldBe TypeSelects.ScalaInt.structure
  }
}
