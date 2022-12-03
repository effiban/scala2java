package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames
import io.github.effiban.scala2java.core.transformers.TermApplyInfixToMapEntryTransformer.transform

import scala.meta.{Lit, Term}

class TermApplyInfixToMapEntryTransformerTest extends UnitTestSuite {

  test("transform") {
    val applyInfix = Term.ApplyInfix(
      lhs = Lit.String("a"),
      targs = Nil,
      op = TermNames.ScalaAssociation,
      args = List(Lit.Int(1))
    )

    val expectedMapEntry = Term.Apply(
      fun = Term.Select(Term.Name("Map"), Term.Name("entry")),
      args = List(Lit.String("a"), Lit.Int(1))
    )

    transform(applyInfix).value.structure shouldBe expectedMapEntry.structure
  }

}
