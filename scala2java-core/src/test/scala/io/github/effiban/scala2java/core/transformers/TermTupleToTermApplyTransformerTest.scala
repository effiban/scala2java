package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.TermTupleToTermApplyTransformer.transform

import scala.meta.{Lit, Term, XtensionQuasiquoteTerm}

class TermTupleToTermApplyTransformerTest extends UnitTestSuite {

  test("transform when 2 args should return java.util.Map.entry(....)") {
    val args = List(Lit.Int(1), Lit.Int(2))
    val expectedTermApply = Term.Apply(q"java.util.Map.entry", args)

    transform(Term.Tuple(args)).structure shouldBe expectedTermApply.structure
  }

  test("transform when 3 args should return org.jooq.lambda.tuple.Tuple.tuple(....)") {
    val args = List(Lit.Int(1), Lit.Int(2), Lit.Int(3))
    val expectedTermApply = Term.Apply(q"org.jooq.lambda.tuple.Tuple.tuple", args)

    transform(Term.Tuple(args)).structure shouldBe expectedTermApply.structure
  }

  test("transform when 4 args should return org.jooq.lambda.tuple.Tuple.tuple(....)") {
    val args = List(
      Lit.Int(1),
      Lit.Int(2),
      Lit.Int(3),
      Lit.Int(4)
    )
    val expectedTermApply = Term.Apply(q"org.jooq.lambda.tuple.Tuple.tuple", args)

    transform(Term.Tuple(args)).structure shouldBe expectedTermApply.structure
  }
}
