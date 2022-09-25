package effiban.scala2java.typeinference

import effiban.scala2java.matchers.CombinedMatchers.eqOptionTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers.any

import scala.meta.Term.{New, Throw}
import scala.meta.{Case, Init, Lit, Name, Term, Type}

class CaseListTypeInferrerTest extends UnitTestSuite {

  private val MyExceptionInit = Init(tpe = Type.Name("MyException1"), name = Name.Anonymous(), argss = Nil)

  private val caseTypeInferrer = mock[CaseTypeInferrer]
  private val collectiveTypeInferrer = mock[CollectiveTypeInferrer]

  private val caseListTypeInferrer = new CaseListTypeInferrerImpl(caseTypeInferrer, collectiveTypeInferrer)

  test("infer for two cases with defined types should return the collective result") {
    val int1 = Lit.Int(1)
    val int2 = Lit.Int(2)

    val case1 = Case(pat = Lit.String("one"), cond = None, body = int1)
    val case2 = Case(pat = Lit.String("two"), cond = None, body = int2)

    when(caseTypeInferrer.infer(any[Case])).thenReturn(Some(TypeNames.Int))
    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(List(Some(TypeNames.Int), Some(TypeNames.Int)))))
      .thenReturn(Some(TypeNames.Int))

    caseListTypeInferrer.infer(List(case1, case2)).value.structure shouldBe TypeNames.Int.structure

    verify(caseTypeInferrer).infer(eqTree(case1))
    verify(caseTypeInferrer).infer(eqTree(case2))
  }

  test("infer for one typed case and one with an anonymous type, should return the collective result") {
    val int = Lit.Int(1)
    val throwException = Throw(New(MyExceptionInit))

    val case1 = Case(pat = Lit.String("one"), cond = None, body = int)
    val case2 = Case(pat = Lit.String("two"), cond = None, body = throwException)

    when(caseTypeInferrer.infer(any[Case])).thenAnswer((`case`: Case) =>
      `case` match {
        case aCase if aCase.body.structure == int.structure => Some(TypeNames.Int)
        case aCase if aCase.body.structure == throwException.structure => Some(Type.AnonymousName())
      })
    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(List(Some(TypeNames.Int), Some(Type.AnonymousName())))))
      .thenReturn(Some(TypeNames.Int))

    caseListTypeInferrer.infer(List(case1, case2)).value.structure shouldBe TypeNames.Int.structure
  }

  test("infer for one typed case and one with an unknown type, should return the collective result") {
    val int = Lit.Int(1)
    val termApply = Term.Apply(fun = Term.Name("blabla"), args = Nil)

    val case1 = Case(pat = Lit.String("one"), cond = None, body = int)
    val case2 = Case(pat = Lit.String("two"), cond = None, body = termApply)

    when(caseTypeInferrer.infer(any[Case])).thenAnswer((`case`: Case) =>
      `case` match {
        case aCase if aCase.body.structure == int.structure => Some(TypeNames.Int)
        case aCase if aCase.body.structure == termApply.structure => None
      })
    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(List(Some(TypeNames.Int), None)))).thenReturn(None)

    caseListTypeInferrer.infer(List(case1, case2)) shouldBe None
  }

  test("infer when empty should return the collective result for an empty list") {
    when(collectiveTypeInferrer.inferScalar(Nil)).thenReturn(Some(Type.AnonymousName()))

    caseListTypeInferrer.infer(Nil).value.structure shouldBe Type.AnonymousName().structure
  }
}
