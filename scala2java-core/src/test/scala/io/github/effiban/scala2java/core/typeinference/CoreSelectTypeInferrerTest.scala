package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TermSelects.{ScalaNil, ScalaNone}
import io.github.effiban.scala2java.core.entities.TypeSelects.{ScalaList, ScalaOption}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.typeinference.CoreSelectTypeInferrer.infer
import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class CoreSelectTypeInferrerTest extends UnitTestSuite {

  test("infer() for 'scala.collection.immutable.Nil' should return 'scala.collection.immutable.List'") {
    infer(ScalaNil, TermSelectInferenceContext()).value.structure shouldBe ScalaList.structure
  }

  test("infer() for 'scala.None' should return 'scala.Option'") {
    infer(ScalaNone, TermSelectInferenceContext()).value.structure shouldBe ScalaOption.structure
  }

  test("infer() for first elem of a Tuple2 should return correct type") {
    val termSelect = q"""("a", 1)._1"""
    val context = TermSelectInferenceContext(Some(t"(String, Int)"))

    infer(termSelect, context).value.structure shouldBe TypeNames.String.structure
  }

  test("infer() for second elem of a Tuple2 should return correct type") {
    val termSelect = q"""("a", 1)._2"""
    val context = TermSelectInferenceContext(Some(t"(String, Int)"))

    infer(termSelect, context).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer() for unrecognized select should return None") {
    val termSelect = q"blabla.gaga"
    val context = TermSelectInferenceContext()

    infer(termSelect, context) shouldBe None

  }
}
