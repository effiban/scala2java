package effiban.scala2java.typeinference

import effiban.scala2java.matchers.CombinedMatchers.{eqOptionTreeList, eqTreeList}
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Lit, Term, Type}

class TermArgsToTypeArgsInferrerImplTest extends UnitTestSuite {

  private val termTypeInferrer = mock[TermTypeInferrer]
  private val tupleTypeInferrer = mock[TupleTypeInferrer]
  private val collectiveTypeInferrer = mock[CollectiveTypeInferrer]

  private val termArgsToTypeArgsInferrer = new TermsToTypeArgsInferrerImpl(
    termTypeInferrer,
    tupleTypeInferrer,
    collectiveTypeInferrer
  )

  test("infer when tuple args") {
    val termTuple1 = Term.Tuple(List(Lit.String("a"), Lit.Int(3)))
    val termTuple2 = Term.Tuple(List(Lit.String("b"), Lit.Int(4)))

    val typeTuple = Type.Tuple(List(TypeNames.String, TypeNames.Int))

    when(tupleTypeInferrer.infer(any[Term.Tuple])).thenAnswer((termTuple: Term.Tuple) => termTuple match {
      case Term.Tuple(List(Lit.String(_), Lit.Int(_))) => typeTuple
      case _ => throw new IllegalStateException(s"Unexpected term tuple $termTuple")
    })

    when(collectiveTypeInferrer.inferTuple(eqTreeList(List(typeTuple, typeTuple)))).thenReturn(typeTuple)

    val inferredTypes = termArgsToTypeArgsInferrer.infer(List(termTuple1, termTuple2))

    inferredTypes.structure shouldBe typeTuple.args.structure
  }

  test("infer when scalar args and collective type found") {
    val term1 = Lit.Int(3)
    val term2 = Lit.Int(4)

    when(termTypeInferrer.infer(any[Term])).thenAnswer((term: Term) => term match {
      case _: Lit.Int => Some(TypeNames.Int)
      case _ => None
    })

    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(List(Some(TypeNames.Int), Some(TypeNames.Int)))))
      .thenReturn(Some(TypeNames.Int))

    val inferredTypes = termArgsToTypeArgsInferrer.infer(List(term1, term2))

    inferredTypes.structure shouldBe List(TypeNames.Int).structure
  }

  test("infer when scalar args and collective type not found should return 'Any'") {
    val term1 = Lit.Int(3)
    val term2 = Lit.String("A")

    when(termTypeInferrer.infer(any[Term])).thenAnswer((term: Term) => term match {
      case _: Lit.Int => Some(TypeNames.Int)
      case _: Lit.String => Some(TypeNames.String)
      case _ => None
    })

    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(List(Some(TypeNames.Int), Some(TypeNames.String)))))
      .thenReturn(None)

    val inferredTypes = termArgsToTypeArgsInferrer.infer(List(term1, term2))

    inferredTypes.structure shouldBe List(TypeNames.Any).structure
  }
}
