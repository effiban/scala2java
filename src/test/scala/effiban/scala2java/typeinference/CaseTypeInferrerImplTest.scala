package effiban.scala2java.typeinference

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames

import scala.meta.{Case, Lit, Term}

class CaseTypeInferrerImplTest extends UnitTestSuite {

  private val termTypeInferrer = mock[TermTypeInferrer]

  private val caseTypeInferrer = new CaseTypeInferrerImpl(termTypeInferrer)

  test("infer should call termTypeInferrer on case body") {
    val body = Term.Name("abc")
    val `case` = Case(
      pat = Lit.Int(3),
      cond = None,
      body = body
    )

    when(termTypeInferrer.infer(eqTree(body))).thenReturn(Some(TypeNames.String))

    caseTypeInferrer.infer(`case`).value.structure shouldBe TypeNames.String.structure
  }

}