package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.predicates.TermNameSupportsNoArgInvocation
import io.github.effiban.scala2java.spi.typeinferrers.NameTypeInferrer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class InternalNameTypeInferrerImplTest extends UnitTestSuite {

  private val applyReturnTypeInferrer = mock[ApplyReturnTypeInferrer]
  private val nameTypeInferrer = mock[NameTypeInferrer]
  private val termNameSupportsNoArgInvocation = mock[TermNameSupportsNoArgInvocation]

  private val internalNameTypeInferrer = new InternalNameTypeInferrerImpl(
    applyReturnTypeInferrer,
    nameTypeInferrer,
    termNameSupportsNoArgInvocation
  )

  test("infer() when no-arg invocation supported, should infer as a Term.Apply with no args and return the result") {
    val termName = q"foo"
    val expectedTermApply = q"foo()"
    val expectedReturnType = TypeNames.Int

    when(termNameSupportsNoArgInvocation(eqTree(termName))).thenReturn(true)
    when(applyReturnTypeInferrer.infer(eqTree(expectedTermApply))).thenReturn(Some(expectedReturnType))

    internalNameTypeInferrer.infer(termName).value.structure shouldBe expectedReturnType.structure
  }

  test("infer() when no-arg invocation supported, should infer as a Term.Apply with no args and return None if that returns None") {
    val termName = q"foo"
    val expectedTermApply = q"foo()"
    val expectedReturnType = TypeNames.Int

    when(termNameSupportsNoArgInvocation(eqTree(termName))).thenReturn(true)
    when(applyReturnTypeInferrer.infer(eqTree(expectedTermApply))).thenReturn(None)

    internalNameTypeInferrer.infer(termName) shouldBe None
  }

  test("infer() when no-arg invocation is not supported, should infer as a name and return the result") {
    val termName = q"foo"
    val expectedReturnType = TypeNames.Int

    when(termNameSupportsNoArgInvocation(eqTree(termName))).thenReturn(false)
    when(nameTypeInferrer.infer(eqTree(termName))).thenReturn(Some(expectedReturnType))

    internalNameTypeInferrer.infer(termName).value.structure shouldBe expectedReturnType.structure
  }

  test("infer() when no-arg invocation is not supported, should infer as a name and return None if that returns None") {
    val termName = q"foo"
    val expectedReturnType = TypeNames.Int

    when(termNameSupportsNoArgInvocation(eqTree(termName))).thenReturn(false)
    when(nameTypeInferrer.infer(eqTree(termName))).thenReturn(None)

    internalNameTypeInferrer.infer(termName) shouldBe None
  }
}
