package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{TermNames, TypeNames}

import scala.meta.Term.ApplyType
import scala.meta.{Term, Type}

class ApplyTypeTypeInferrerImplTest extends UnitTestSuite {

  private val termTypeInferrer = mock[TermTypeInferrer]

  private val applyTypeTypeInferrer = new ApplyTypeTypeInferrerImpl(termTypeInferrer)

  test("infer when 'fun' can be inferred") {
    val typeArgs = List(TypeNames.String, TypeNames.Int)
    val applyType = ApplyType(fun = TermNames.Map, targs = typeArgs)

    when(termTypeInferrer.infer(eqTree(TermNames.Map))).thenReturn(Some(TypeNames.Map))

    applyTypeTypeInferrer.infer(applyType).value.structure shouldBe Type.Apply(TypeNames.Map, typeArgs).structure
  }

  test("infer when 'fun' cannot be inferred") {
    val typeArgs = List(TypeNames.String)
    val applyType = ApplyType(fun = Term.Name("foo"), targs = typeArgs)

    when(termTypeInferrer.infer(eqTree(Term.Name("foo")))).thenReturn(None)

    applyTypeTypeInferrer.infer(applyType) shouldBe None

  }
}
