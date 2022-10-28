package io.github.effiban.scala2java.transformers

import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.TermNames
import io.github.effiban.scala2java.testtrees.TermNames.{Empty, ScalaNil, ScalaNone, ScalaOption}

import scala.meta.Term

class TermNameTransformerTest extends UnitTestSuite {

  private val arbitraryName = Term.Name("blabla")

  private final val TermNameToTermSelectMappings = Table(
    ("Term Name", "Term"),
    (ScalaNone, Term.Select(ScalaOption, Empty)),
    (ScalaNil, Term.Select(TermNames.List, Empty)),
    (arbitraryName, arbitraryName)
  )

  forAll(TermNameToTermSelectMappings) { (termName: Term.Name, expectedTerm: Term) =>
    test(s"transform $termName should return $expectedTerm") {
      TermNameTransformer.transform(termName).structure shouldBe expectedTerm.structure
    }
  }
}
