package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{TermNames, TypeNames}

import scala.meta.{Term, Type}

class CoreNameTypeInferrerTest extends UnitTestSuite {

  private val TermNameToTypeNameMappings = Table(
    ("TermName", "ExpectedMaybeTypeName"),
    (TermNames.ScalaOption, Some(TypeNames.ScalaOption)),
    (TermNames.ScalaSome, Some(TypeNames.ScalaOption)),
    (TermNames.ScalaNone, Some(TypeNames.ScalaOption)),
    (TermNames.ScalaRight, Some(TypeNames.Either)),
    (TermNames.ScalaLeft, Some(TypeNames.Either)),
    (TermNames.Try, Some(TypeNames.Try)),
    (TermNames.ScalaSuccess, Some(TypeNames.Try)),
    (TermNames.ScalaFailure, Some(TypeNames.Try)),
    (TermNames.Future, Some(TypeNames.Future)),
    (TermNames.Stream, Some(TypeNames.Stream)),
    (TermNames.ScalaArray, Some(TypeNames.ScalaArray)),
    (TermNames.List, Some(TypeNames.List)),
    (TermNames.ScalaNil, Some(TypeNames.List)),
    (TermNames.Vector, Some(TypeNames.ScalaVector)),
    (TermNames.Seq, Some(TypeNames.Seq)),
    (TermNames.Set, Some(TypeNames.Set)),
    (TermNames.Map, Some(TypeNames.Map)),
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
