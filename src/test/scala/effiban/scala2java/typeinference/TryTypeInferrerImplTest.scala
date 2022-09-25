package effiban.scala2java.typeinference

import effiban.scala2java.matchers.CombinedMatchers.{eqOptionTreeList, eqTreeList}
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers.any

import scala.meta.{Case, Lit, Pat, Term, Type}

class TryTypeInferrerImplTest extends UnitTestSuite {

  private val TryStatement = Term.Apply(Term.Name("doSomething"), Nil)

  private val CatchPat1 = Pat.Typed(Term.Name("e1"), Type.Name("MyException1"))
  private val CatchPat2 = Pat.Typed(Term.Name("e2"), Type.Name("MyException2"))

  private val CatchBody1 = Lit.String("abc")
  private val CatchBody2 = Lit.String("def")

  private val CatchCase1 = Case(
    pat = CatchPat1,
    cond = None,
    body = CatchBody1
  )
  private val CatchCase2 = Case(
    pat = CatchPat2,
    cond = None,
    body = CatchBody2
  )

  private val CatchCases = List(CatchCase1, CatchCase2)

  private val FinallyStatement = Term.Apply(Term.Name("cleanup"), Nil)

  private val termTypeInferrer = mock[TermTypeInferrer]
  private val caseListTypeInferrer = mock[CaseListTypeInferrer]
  private val collectiveTypeInferrer = mock[CollectiveTypeInferrer]

  private val tryTypeInferrer =  new TryTypeInferrerImpl(
    termTypeInferrer,
    caseListTypeInferrer,
    collectiveTypeInferrer
  )

  test("infer when has a 'try' expression only should return the collective type of: 'try', empty case list, and anonymous") {
    val `try` = Term.Try(
      expr = TryStatement,
      catchp = Nil,
      finallyp = None
    )

    when(termTypeInferrer.infer(eqTree(TryStatement))).thenReturn(Some(TypeNames.String))
    when(caseListTypeInferrer.infer(Nil)).thenReturn(Some(Type.AnonymousName()))
    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(List(Some(TypeNames.String), Some(Type.AnonymousName()), Some(Type.AnonymousName())))))
      .thenReturn(Some(TypeNames.String))

    tryTypeInferrer.infer(`try`).value.structure shouldBe TypeNames.String.structure
  }

  test("infer when has a 'try' expression and catch cases should return the collective type of: 'try' expr, catch cases, and anonymous") {
    val `try` = Term.Try(
      expr = TryStatement,
      catchp = CatchCases,
      finallyp = None
    )

    when(termTypeInferrer.infer(eqTree(TryStatement))).thenReturn(Some(TypeNames.String))
    when(caseListTypeInferrer.infer(eqTreeList(CatchCases))).thenReturn(Some(TypeNames.String))
    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(List(Some(TypeNames.String), Some(TypeNames.String), Some(Type.AnonymousName())))))
      .thenReturn(Some(TypeNames.String))

    tryTypeInferrer.infer(`try`).value.structure shouldBe TypeNames.String.structure
  }

  test("infer when has 'try' and finally expressions should return the collective type of: 'try' expr, empty case list, and 'finally' expr") {
    val `try` = Term.Try(
      expr = TryStatement,
      catchp = Nil,
      finallyp = Some(FinallyStatement)
    )

    val collectiveType = Type.Name("Any")

    when(termTypeInferrer.infer(any[Term])).thenAnswer( (term: Term) => {
      term match {
        case t if t.structure == TryStatement.structure => Some(TypeNames.String)
        case t if t.structure == FinallyStatement.structure => Some(TypeNames.Int)
      }
    })
    when(caseListTypeInferrer.infer(Nil)).thenReturn(Some(Type.AnonymousName()))
    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(List(Some(TypeNames.String), Some(Type.AnonymousName()), Some(TypeNames.Int)))))
      .thenReturn(Some(collectiveType))

    tryTypeInferrer.infer(`try`).value.structure shouldBe collectiveType.structure
  }

  test("infer when has all three parts should return the collective type of all") {
    val `try` = Term.Try(
      expr = TryStatement,
      catchp = CatchCases,
      finallyp = Some(FinallyStatement)
    )

    val collectiveType = Type.Name("Any")

    when(termTypeInferrer.infer(any[Term])).thenAnswer((term: Term) => {
      term match {
        case t if t.structure == TryStatement.structure => Some(TypeNames.String)
        case t if t.structure == FinallyStatement.structure => Some(TypeNames.Int)
      }
    })
    when(caseListTypeInferrer.infer(eqTreeList(CatchCases))).thenReturn(Some(TypeNames.String))
    when(collectiveTypeInferrer.inferScalar(eqOptionTreeList(List(Some(TypeNames.String), Some(TypeNames.String), Some(TypeNames.Int)))))
      .thenReturn(Some(collectiveType))

    tryTypeInferrer.infer(`try`).value.structure shouldBe collectiveType.structure
  }
}
