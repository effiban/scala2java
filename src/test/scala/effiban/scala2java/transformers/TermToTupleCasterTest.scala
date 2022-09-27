package effiban.scala2java.transformers

import effiban.scala2java.entities.ScalaOperatorName
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Lit, Term}

class TermToTupleCasterTest extends UnitTestSuite {

  test("cast when term is already a Term.Tuple should return it") {
    val termTuple = Term.Tuple(List(Lit.Int(1), Lit.Int(2)))
    TermToTupleCaster.cast(termTuple).structure shouldBe termTuple.structure
  }

  test("cast when term is an Term.ApplyInfix should return a tuple composed of the args") {
    val applyInfix = Term.ApplyInfix(
      lhs = Lit.String("a"),
      targs = Nil,
      op = Term.Name(ScalaOperatorName.Associate),
      args = List(Lit.Int(1))
    )
    val expectedTermTuple = Term.Tuple(List(Lit.String("a"), Lit.Int(1)))

    TermToTupleCaster.cast(applyInfix).structure shouldBe expectedTermTuple.structure
  }

  test("cast when term is a Term.Name should throw an exception") {
    intercept[IllegalStateException] {
      TermToTupleCaster.cast(Term.Name("a"))
    }
  }
}
