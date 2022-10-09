package io.github.effiban.scala2java.typeinference

import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Lit, Term, Type}

class TupleTypeInferrerImplTest extends UnitTestSuite {

  private val termTypeInferrer = mock[TermTypeInferrer]

  private val tupleTypeInferrer = new TupleTypeInferrerImpl(termTypeInferrer)

  test("infer when all terms are inferrable") {
    val termTuple = Term.Tuple(List(Lit.String("a"), Lit.Int(1)))

    when(termTypeInferrer.infer(any[Term])).thenAnswer( (term: Term) => term match {
      case _: Lit.String => Some(TypeNames.String)
      case _: Lit.Int => Some(TypeNames.Int)
      case _ => None
    })

    tupleTypeInferrer.infer(termTuple).structure shouldBe Type.Tuple(List(TypeNames.String, TypeNames.Int)).structure
  }

  test("infer when some terms are not inferrable") {
    val termTuple = Term.Tuple(List(Term.Name("foo"), Lit.Int(1)))

    when(termTypeInferrer.infer(any[Term])).thenAnswer((term: Term) => term match {
      case _: Lit.Int => Some(TypeNames.Int)
      case _ => None
    })

    tupleTypeInferrer.infer(termTuple).structure shouldBe Type.Tuple(List(TypeNames.ScalaAny, TypeNames.Int)).structure
  }
}
