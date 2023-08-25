package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TypeSelects.{ScalaList, ScalaOption}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames
import io.github.effiban.scala2java.core.typeinference.CoreNameTypeInferrer.infer

import scala.meta.{Term, Type}

class CoreNameTypeInferrerTest extends UnitTestSuite {

  private val TermNameToTypeMappings = Table(
    ("TermName", "ExpectedMaybeType"),
    (TermNames.ScalaNone, Some(ScalaOption)),
    (TermNames.ScalaNil, Some(ScalaList)),
    (Term.Name("foo"), None)
  )

  forAll(TermNameToTypeMappings) {
    (termName: Term.Name, expectedMaybeType: Option[Type]) => {
      test(s"Infer $termName should return $expectedMaybeType") {
        expectedMaybeType match {
          case Some(expectedTypeName) => infer(termName).value.structure shouldBe expectedTypeName.structure
          case None => infer(termName) shouldBe None
        }
      }
    }
  }

}
