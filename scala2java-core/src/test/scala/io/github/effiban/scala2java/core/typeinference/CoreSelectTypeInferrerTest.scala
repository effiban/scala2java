package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TermSelects.{ScalaNil, ScalaNone}
import io.github.effiban.scala2java.core.entities.TypeSelects.{ScalaList, ScalaOption}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.CoreSelectTypeInferrer.infer
import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext

import scala.meta.XtensionQuasiquoteTerm

class CoreSelectTypeInferrerTest extends UnitTestSuite {

  test("infer() for 'scala.collection.immutable.Nil' should return 'scala.collection.immutable.List'") {
    infer(ScalaNil, TermSelectInferenceContext()).value.structure shouldBe ScalaList.structure
  }

  test("infer() for 'scala.None' should return 'scala.Option'") {
    infer(ScalaNone, TermSelectInferenceContext()).value.structure shouldBe ScalaOption.structure
  }

  test("infer() for unrecognized select should return None") {
    val termSelect = q"blabla.gaga"
    val context = TermSelectInferenceContext()

    infer(termSelect, context) shouldBe None

  }
}
