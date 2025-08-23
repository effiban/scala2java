package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.factories.TermApplyInferenceContextFactory
import io.github.effiban.scala2java.core.matchers.PartialDeclDefScalatestMatcher.equalPartialDeclDef
import io.github.effiban.scala2java.core.matchers.TermApplyInferenceContextMockitoMatcher.eqTermApplyInferenceContext
import io.github.effiban.scala2java.core.predicates.TermSelectHasApplyMethod
import io.github.effiban.scala2java.core.reflection.ScalaReflectionMethodSignatureInferrer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.spi.typeinferrers.ApplyDeclDefInferrer
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqOptionTreeMultiList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class InternalApplyDeclDefInferrerImpl_ByReflection_MultiArgListTest extends UnitTestSuite {

  private val ParentType = t"Parent"

  private val applyDeclDefInferrer = mock[ApplyDeclDefInferrer]
  private val termSelectHasApplyMethod = mock[TermSelectHasApplyMethod]
  private val scalaReflectionMethodSignatureInferrer = mock[ScalaReflectionMethodSignatureInferrer]
  private val termApplyInferenceContextFactory = mock[TermApplyInferenceContextFactory]

  private val internalApplyDeclDefInferrer = new InternalApplyDeclDefInferrerImpl(
    applyDeclDefInferrer,
    termSelectHasApplyMethod,
    scalaReflectionMethodSignatureInferrer,
    termApplyInferenceContextFactory
  )

  test(
    """
    infer() when parent type inferred,
    and inferred successfully as a method with two arg lists
    """) {
    val termApply = q"a.b(x1, x2)(y1, y2)"
    val innerTermApply = q"a.b(x1, x2)"
    val allArgTypes = List(
      List(t"scala.Int", t"scala.Long"),
      List(t"scala.Int", t"scala.Long")
    )
    val context = TermApplyInferenceContext(
      maybeParentType = Some(ParentType),
      maybeArgTypeLists = List(List(Some(t"scala.Int"), Some(t"scala.Long")))
    )

    val expectedPartialDeclDef = PartialDeclDef(
      maybeParamTypeLists = allArgTypes.map(_.map(Some(_))),
      maybeReturnType = Some(t"java.lang.String")
    )

    when(termSelectHasApplyMethod(any[Term.Select])).thenReturn(false)

    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTermApplyInferenceContext(context))).thenReturn(PartialDeclDef())

    doAnswer( (_: Type, _: Term, argTypeLists: List[List[Type]]) => {
      argTypeLists match {
        case List(
          List(t"scala.Int", t"scala.Long"),
          List(t"scala.Int", t"scala.Long")
        ) => expectedPartialDeclDef
        case _ => PartialDeclDef()
      }
    }).when(scalaReflectionMethodSignatureInferrer).inferPartialMethodSignature(
      eqTree(ParentType),
      eqTree(q"b"),
      any[List[List[Type]]]()
    )

    when(termApplyInferenceContextFactory.create(eqTree(innerTermApply), eqOptionTreeMultiList(context.maybeArgTypeLists)))
      .thenReturn(TermApplyInferenceContext(
        maybeParentType = Some(ParentType),
        maybeArgTypeLists = allArgTypes.map(_.map(Some(_)))
      ))

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(expectedPartialDeclDef)
  }

  test(
    """
    infer() when parent type inferred,
    and has a nested method invocation,
    and NOT inferred as a multi-arg-list method
    """) {
    val termApply = q"a.b(x1, x2)(y1, y2)"
    val innerTermApply = q"a.b(x1, x2)"
    val allArgTypes = List(
      List(t"scala.Int", t"scala.Long"),
      List(t"scala.Int", t"scala.Long")
    )
    val context = TermApplyInferenceContext(
      maybeParentType = Some(ParentType),
      maybeArgTypeLists = List(List(Some(t"scala.Int"), Some(t"scala.Long")))
    )

    when(applyDeclDefInferrer.infer(eqTree(termApply), eqTermApplyInferenceContext(context))).thenReturn(PartialDeclDef())

    doReturn(PartialDeclDef())
      .when(scalaReflectionMethodSignatureInferrer).inferPartialMethodSignature(
        eqTree(ParentType),
        eqTree(q"b"),
        any[List[List[Type]]]()
      )

    when(termApplyInferenceContextFactory.create(eqTree(innerTermApply), eqOptionTreeMultiList(context.maybeArgTypeLists)))
      .thenReturn(TermApplyInferenceContext(
        maybeParentType = Some(ParentType),
        maybeArgTypeLists = allArgTypes.map(_.map(Some(_)))
      ))

    internalApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(PartialDeclDef())
  }
}
