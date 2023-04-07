package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames
import io.github.effiban.scala2java.core.testtrees.TermNames.{Empty, ScalaNil, ScalaNone, ScalaOption}
import io.github.effiban.scala2java.core.transformers.CoreTermNameTransformer.transform

import scala.meta.{Term, XtensionQuasiquoteTerm}

class CoreTermNameTransformerTest extends UnitTestSuite {

  private final val MappedScenarios = Table(
    ("Term Name", "Expected Term"),
    (ScalaNone, Term.Select(ScalaOption, Empty)),
    (ScalaNil, Term.Select(TermNames.List, Empty))
  )

  private final val UnmappedScenarios = Table(
    "Term Name",
    TermNames.Print,
    q"blabla"
  )

  forAll(MappedScenarios) { (termName: Term.Name, expectedTerm: Term) =>
    test(s"transform $termName should return $expectedTerm") {
      transform(termName).value.structure shouldBe expectedTerm.structure
    }
  }

  forAll(UnmappedScenarios) { (termName: Term.Name) =>
    test(s"transform $termName should return None") {
      transform(termName) shouldBe None
    }
  }
}
