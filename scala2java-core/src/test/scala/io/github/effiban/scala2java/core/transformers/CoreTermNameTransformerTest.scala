package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames
import io.github.effiban.scala2java.core.testtrees.TermNames._
import io.github.effiban.scala2java.core.transformers.CoreTermNameTransformer.transform

import scala.meta.{Term, XtensionQuasiquoteTerm}

class CoreTermNameTransformerTest extends UnitTestSuite {

  private final val MappedScenarios = Table(
    ("Term Name", "Expected Term"),
    (ScalaNone, Term.Apply(Term.Select(JavaOptional, JavaAbsent), Nil)),
    (ScalaNil, Term.Apply(Term.Select(TermNames.List, JavaOf), Nil))
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
