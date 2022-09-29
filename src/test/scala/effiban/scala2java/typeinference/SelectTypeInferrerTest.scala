package effiban.scala2java.typeinference

import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.{TermNames, TypeNames}
import effiban.scala2java.typeinference.SelectTypeInferrer.infer

import scala.meta.{Term, Type}

class SelectTypeInferrerTest extends UnitTestSuite {

  private val TermSelectToMaybeTypeMappings = Table(
    ("TermSelect", "MaybeType"),
    (Term.Select(TermNames.Future, TermNames.ScalaSuccessful), Some(TypeNames.Future)),
    (Term.Select(TermNames.Future, TermNames.ScalaFailed), Some(TypeNames.Future)),
    (Term.Select(Term.Name("foo"), Term.Name("bar")), None)
  )

  forAll(TermSelectToMaybeTypeMappings) {
    (termSelect: Term.Select, expectedMaybeType: Option[Type]) =>
      test(s"Infer $termSelect should return $expectedMaybeType") {
        expectedMaybeType match {
          case Some(expectedType) => infer(termSelect).value.structure shouldBe expectedType.structure
          case None => infer(termSelect) shouldBe None
        }
      }
  }
}
