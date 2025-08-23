package io.github.effiban.scala2java.core.factories

import io.github.effiban.scala2java.core.entities.TypeSelects
import io.github.effiban.scala2java.core.matchers.TermApplyInferenceContextScalatestMatcher.equalTermApplyInferenceContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.{ApplyParentTypeInferrer, TermTypeInferrer}
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Term, XtensionQuasiquoteTerm}

class TermApplyInferenceContextFactoryImplTest extends UnitTestSuite {

  private val applyParentTypeInferrer = mock[ApplyParentTypeInferrer]
  private val termTypeInferrer = mock[TermTypeInferrer]

  private val termApplyInferenceContextFactory = new TermApplyInferenceContextFactoryImpl(applyParentTypeInferrer, termTypeInferrer)

  test("create() when has no additional arg lists") {
    val fun = q"foo"
    val arg1 = q""""a""""
    val arg2 = q""""b""""
    val termApply = Term.Apply(fun, List(arg1, arg2))

    val expectedFunType = TypeSelects.ScalaInt
    val expectedArg1Type = TypeSelects.ScalaLong
    val expectedArg2Type = TypeSelects.JavaString
    val expectedMaybeArgTypes = List(Some(expectedArg1Type), Some(expectedArg2Type))

    val expectedInferenceContext = TermApplyInferenceContext(
      maybeParentType = Some(expectedFunType),
      maybeArgTypeLists = List(expectedMaybeArgTypes)
    )

    when(applyParentTypeInferrer.infer(eqTree(termApply))).thenReturn(Some(expectedFunType))

    when(termTypeInferrer.infer(any[Term])).thenAnswer((term: Term) => term match {
      case arg if arg.structure ==  arg1.structure => Some(expectedArg1Type)
      case arg if arg.structure ==  arg2.structure => Some(expectedArg2Type)
      case _ => None
    })

    termApplyInferenceContextFactory.create(termApply) should equalTermApplyInferenceContext(expectedInferenceContext)
  }

  test("create() when has two additional arg lists") {
    val fun = q"foo"
    val arg1 = q"a"
    val arg2 = q"b"
    val arg3 = q"c"
    val arg4 = q"d"
    val arg5 = q"e"
    val arg6 = q"f"

    val expectedFunType = TypeSelects.ScalaInt
    val expectedArg1Type = TypeSelects.ScalaInt
    val expectedArg2Type = TypeSelects.ScalaLong
    val arg3Type = TypeSelects.ScalaShort
    val arg4Type = TypeSelects.ScalaFloat
    val arg5Type = TypeSelects.ScalaDouble
    val arg6Type = TypeSelects.ScalaBoolean

    val termApply = Term.Apply(fun, List(arg1, arg2))

    val additionalMaybeArgTypeLists = List(
      List(Some(arg3Type), Some(arg4Type)),
      List(Some(arg5Type), Some(arg6Type))
    )

    val expectedMaybeArgTypeLists = List(
      List(Some(expectedArg1Type), Some(expectedArg2Type)),
      List(Some(arg3Type), Some(arg4Type)),
      List(Some(arg5Type), Some(arg6Type))
    )

    val expectedInferenceContext = TermApplyInferenceContext(
      maybeParentType = Some(expectedFunType),
      maybeArgTypeLists = expectedMaybeArgTypeLists
    )

    when(applyParentTypeInferrer.infer(eqTree(termApply))).thenReturn(Some(expectedFunType))

    when(termTypeInferrer.infer(any[Term])).thenAnswer((term: Term) => term match {
      case arg if arg.structure ==  arg1.structure => Some(expectedArg1Type)
      case arg if arg.structure ==  arg2.structure => Some(expectedArg2Type)
      case _ => None
    })

    termApplyInferenceContextFactory.create(termApply, additionalMaybeArgTypeLists) should
      equalTermApplyInferenceContext(expectedInferenceContext)
  }
}
