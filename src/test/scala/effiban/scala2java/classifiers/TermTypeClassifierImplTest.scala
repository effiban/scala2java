package effiban.scala2java.classifiers

import effiban.scala2java.entities.Decision.{No, Uncertain, Yes}
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import effiban.scala2java.typeinference.TermTypeInferrer

import scala.meta.{Lit, Term, Type}

class TermTypeClassifierImplTest extends UnitTestSuite {

  private val termTypeInferrer = mock[TermTypeInferrer]

  private val termTypeClassifier = new TermTypeClassifierImpl(termTypeInferrer)

  test("isReturnable() when inferred type is Unit should return No") {
    when(termTypeInferrer.infer(eqTree(Lit.Unit()))).thenReturn(Some(TypeNames.Unit))
    termTypeClassifier.isReturnable(Lit.Unit()) shouldBe No
  }

  test("isReturnable() when inferred type is Anonymous should return No") {
    val term = Term.Name("anon")
    when(termTypeInferrer.infer(eqTree(term))).thenReturn(Some(Type.AnonymousName()))
    termTypeClassifier.isReturnable(term) shouldBe No
  }

  test("isReturnable() when inferred type is Int should return Yes") {
    val litInt = Lit.Int(3)
    when(termTypeInferrer.infer(eqTree(litInt))).thenReturn(Some(TypeNames.Int))
    termTypeClassifier.isReturnable(litInt) shouldBe Yes
  }

  test("isReturnable() when inferred type is String should return Yes") {
    val litString = Lit.String("abc")
    when(termTypeInferrer.infer(eqTree(litString))).thenReturn(Some(TypeNames.String))
    termTypeClassifier.isReturnable(litString) shouldBe Yes
  }

  test("isReturnable() when inferred type is None should return Uncertain") {
    val term = Term.Apply(Term.Name("foo"), List(Lit.String("bar")))
    when(termTypeInferrer.infer(eqTree(term))).thenReturn(None)
    termTypeClassifier.isReturnable(term) shouldBe Uncertain
  }
}
