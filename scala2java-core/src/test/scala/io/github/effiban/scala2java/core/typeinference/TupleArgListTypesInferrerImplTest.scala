package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Lit, Term, Type}

class TupleArgListTypesInferrerImplTest extends UnitTestSuite {

  private val tupleTypeInferrer = mock[TupleTypeInferrer]
  private val collectiveTypeInferrer = mock[CollectiveTypeInferrer]

  private val tupleArgListTypesInferrer = new TupleArgListTypesInferrerImpl(
    tupleTypeInferrer,
    collectiveTypeInferrer
  )

  test("infer") {
    val termTuple1 = Term.Tuple(List(Lit.String("a"), Lit.Int(3)))
    val termTuple2 = Term.Tuple(List(Lit.String("b"), Lit.Int(4)))

    val typeTuple = Type.Tuple(List(TypeNames.String, TypeNames.Int))

    when(tupleTypeInferrer.infer(any[Term.Tuple])).thenAnswer((termTuple: Term.Tuple) => termTuple match {
      case Term.Tuple(List(Lit.String(_), Lit.Int(_))) => typeTuple
      case _ => throw new IllegalStateException(s"Unexpected term tuple $termTuple")
    })

    when(collectiveTypeInferrer.inferTuple(eqTreeList(List(typeTuple, typeTuple)))).thenReturn(typeTuple)

    val inferredTypes = tupleArgListTypesInferrer.infer(List(termTuple1, termTuple2))

    inferredTypes.structure shouldBe typeTuple.args.structure
  }
}
