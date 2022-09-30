package effiban.scala2java.typeinference

import effiban.scala2java.matchers.CombinedMatchers.eqOptionTreeList
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Lit, Term}

class ScalarArgListTypeInferrerImplTest extends UnitTestSuite {

  private val termTypeInferrer = mock[TermTypeInferrer]
  private val collectiveTypeInferrer = mock[CollectiveTypeInferrer]

  private val scalarArgListTypeInferrer = new ScalarArgListTypeInferrerImpl(
    termTypeInferrer,
    collectiveTypeInferrer
  )


  test("infer when collective type found") {
    val term1 = Lit.Int(3)
    val term2 = Lit.Int(4)

    when(termTypeInferrer.infer(any[Term])).thenAnswer((term: Term) => term match {
      case _: Lit.Int => Some(TypeNames.Int)
      case _ => None
    })

    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(List(Some(TypeNames.Int), Some(TypeNames.Int)))))
      .thenReturn(Some(TypeNames.Int))

    val inferredTypes = scalarArgListTypeInferrer.infer(List(term1, term2))

    inferredTypes.structure shouldBe TypeNames.Int.structure
  }

  test("infer when collective type not found should return 'Any'") {
    val term1 = Lit.Int(3)
    val term2 = Lit.String("A")

    when(termTypeInferrer.infer(any[Term])).thenAnswer((term: Term) => term match {
      case _: Lit.Int => Some(TypeNames.Int)
      case _: Lit.String => Some(TypeNames.String)
      case _ => None
    })

    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(List(Some(TypeNames.Int), Some(TypeNames.String)))))
      .thenReturn(None)

    val inferredTypes = scalarArgListTypeInferrer.infer(List(term1, term2))

    inferredTypes.structure shouldBe TypeNames.ScalaAny.structure
  }
}
