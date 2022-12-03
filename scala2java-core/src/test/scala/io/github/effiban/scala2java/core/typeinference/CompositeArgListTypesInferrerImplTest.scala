package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.TermTypeClassifier
import io.github.effiban.scala2java.core.entities.TermNameValues.ScalaAssociate
import io.github.effiban.scala2java.core.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.transformers.TermToTupleCaster
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Lit, Term}

class CompositeArgListTypesInferrerImplTest extends UnitTestSuite {

  private val scalarArgListTypeInferrer = mock[ScalarArgListTypeInferrer]
  private val tupleArgListTypesInferrer = mock[TupleArgListTypesInferrer]
  private val termTypeClassifier = mock[TermTypeClassifier]
  private val termToTupleCaster = mock[TermToTupleCaster]

  private val compositeArgListTypesInferrer = new CompositeArgListTypesInferrerImpl(
    scalarArgListTypeInferrer,
    tupleArgListTypesInferrer,
    termTypeClassifier,
    termToTupleCaster
  )

  test("infer when args are all scalars") {
    val args = List(Lit.Int(1), Lit.Int(2))

    when(termTypeClassifier.isTupleLike(any[Lit])).thenReturn(false)
    when(scalarArgListTypeInferrer.infer(eqTreeList(args))).thenReturn(TypeNames.Int)

    compositeArgListTypesInferrer.infer(args).structure shouldBe List(TypeNames.Int).structure
  }

  test("infer when args are all regular tuples") {
    val tuple1 = Term.Tuple(List(Lit.String("a"), Lit.Int(1)))
    val tuple2 = Term.Tuple(List(Lit.String("b"), Lit.Int(2)))
    val args = List(tuple1, tuple2)

    when(termTypeClassifier.isTupleLike(any[Term.Tuple])).thenReturn(true)
    when(termToTupleCaster.cast(any[Term.Tuple])).thenAnswer( (termTuple: Term.Tuple) => termTuple)

    when(tupleArgListTypesInferrer.infer(eqTreeList(args))).thenReturn(List(TypeNames.String, TypeNames.Int))

    compositeArgListTypesInferrer.infer(args).structure shouldBe List(TypeNames.String, TypeNames.Int).structure
  }

  test("infer when args are a combination of regular tuples and associations") {
    val arg1 = Term.Tuple(List(Lit.String("a"), Lit.Int(1)))
    val arg2 = Term.ApplyInfix(
      lhs = Lit.String("b"),
      op = Term.Name(ScalaAssociate),
      targs = Nil,
      args = List(Lit.Int(2))
    )
    val arg3 = Term.Tuple(List(Lit.String("c"), Lit.Int(3)))

    val args = List(arg1, arg2, arg3)

    val expectedTuple2 = Term.Tuple(List(Lit.String("b"), Lit.Int(2)))

    when(termTypeClassifier.isTupleLike(any[Term.Tuple])).thenReturn(true)
    when(termToTupleCaster.cast(any[Term])).thenAnswer((term: Term) => term match {
      case tuple: Term.Tuple => tuple
      case applyInfix: Term.ApplyInfix => Term.Tuple(List(Lit.String("b"), Lit.Int(2)))
    })

    val expectedTuples = List(arg1, expectedTuple2, arg3)

    when(tupleArgListTypesInferrer.infer(eqTreeList(expectedTuples))).thenReturn(List(TypeNames.String, TypeNames.Int))

    compositeArgListTypesInferrer.infer(args).structure shouldBe List(TypeNames.String, TypeNames.Int).structure
  }
}
