package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{TermNames, TypeNames}
import io.github.effiban.scala2java.core.typeinference.CoreSelectTypeInferrer.infer
import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext

import scala.meta.{Term, Type, XtensionQuasiquoteTerm}

class CoreSelectTypeInferrerTest extends UnitTestSuite {

  private val TermSelectToMaybeTypeMappings = Table(
    ("TermSelect", "MaybeType"),
    (Term.Select(TermNames.Future, TermNames.ScalaSuccessful), Some(TypeNames.Future)),
    (Term.Select(TermNames.Future, TermNames.ScalaFailed), Some(TypeNames.Future)),
    (Term.Select(q"obj", q"toString"), Some(TypeNames.String)),
    (Term.Select(Term.Name("foo"), Term.Name("bar")), None)
  )

  forAll(TermSelectToMaybeTypeMappings) {
    (termSelect: Term.Select, expectedMaybeType: Option[Type]) =>
      test(s"Infer $termSelect should return $expectedMaybeType") {
        expectedMaybeType match {
          case Some(expectedType) => infer(termSelect, TermSelectInferenceContext()).value.structure shouldBe expectedType.structure
          case None => infer(termSelect, TermSelectInferenceContext()) shouldBe None
        }
      }
  }
}
