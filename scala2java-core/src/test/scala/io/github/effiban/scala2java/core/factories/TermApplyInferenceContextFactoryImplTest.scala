package io.github.effiban.scala2java.core.factories

import io.github.effiban.scala2java.core.matchers.TermApplyInferenceContextScalatestMatcher.equalTermApplyInferenceContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.typeinference.{ApplyParentTypeInferrer, TermTypeInferrer}
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Term, XtensionQuasiquoteTerm}

class TermApplyInferenceContextFactoryImplTest extends UnitTestSuite {

  private val applyParentTypeInferrer = mock[ApplyParentTypeInferrer]
  private val termTypeInferrer = mock[TermTypeInferrer]

  private val termApplyInferenceContextFactory = new TermApplyInferenceContextFactoryImpl(applyParentTypeInferrer, termTypeInferrer)

  test("create()") {
    val fun = q"foo"
    val arg1 = q""""a""""
    val arg2 = q""""b""""
    val termApply = Term.Apply(fun, List(arg1, arg2))

    val expectedFunType = TypeNames.Int
    val expectedArg1Type = TypeNames.Long
    val expectedArg2Type = TypeNames.String
    val expectedMaybeArgTypes = List(Some(expectedArg1Type), Some(expectedArg2Type))

    val expectedInferenceContext = TermApplyInferenceContext(
      maybeParentType = Some(expectedFunType),
      maybeArgTypes = expectedMaybeArgTypes
    )

    when(applyParentTypeInferrer.infer(eqTree(termApply))).thenReturn(Some(expectedFunType))

    when(termTypeInferrer.infer(any[Term])).thenAnswer((term: Term) => term match {
      case arg if arg.structure ==  arg1.structure => Some(expectedArg1Type)
      case arg if arg.structure ==  arg2.structure => Some(expectedArg2Type)
      case _ => None
    })

    termApplyInferenceContextFactory.create(termApply) should equalTermApplyInferenceContext(expectedInferenceContext)
  }
}
