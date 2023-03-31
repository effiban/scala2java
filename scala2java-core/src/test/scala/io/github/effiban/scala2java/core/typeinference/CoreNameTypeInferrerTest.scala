package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{TermNames, TypeNames}

import scala.meta.{Term, Type}

class CoreNameTypeInferrerTest extends UnitTestSuite {

  private val TermNameToTypeNameMappings = Table(
    ("TermName", "ExpectedMaybeTypeName"),
    (TermNames.ScalaNone, Some(TypeNames.ScalaOption)),
    (TermNames.ScalaNil, Some(TypeNames.List)),
    (Term.Name("foo"), None)
  )

  forAll(TermNameToTypeNameMappings) {
    (termName: Term.Name, expectedMaybeTypeName: Option[Type.Name]) => {
      test(s"Infer $termName should return $expectedMaybeTypeName") {
        expectedMaybeTypeName match {
          case Some(expectedTypeName) => CoreNameTypeInferrer.infer(termName).value.structure shouldBe expectedTypeName.structure
          case None => CoreNameTypeInferrer.infer(termName) shouldBe None
        }
      }
    }
  }

}
