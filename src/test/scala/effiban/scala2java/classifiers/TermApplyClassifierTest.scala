package effiban.scala2java.classifiers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.{TermNames, TypeNames}

import scala.meta.{Lit, Term}

class TermApplyClassifierTest extends UnitTestSuite {

  private val scalaCollectionNameClassifier = mock[TermNameClassifier]

  private val termApplyClassifier = new TermApplyClassifierImpl(scalaCollectionNameClassifier)

  test("isCollectionInitializer() when method name is an untyped collection - should return true") {
    val termName = TermNames.List
    val termApply = Term.Apply(termName, List(Lit.Int(3)))

    when(scalaCollectionNameClassifier.isCollection(eqTree(termName))).thenReturn(true)

    termApplyClassifier.isCollectionInitializer(termApply) shouldBe true
  }

  test("isCollectionInitializer() when method name is a typed collection - should return true") {
    val termName = TermNames.Set
    val termApply = Term.Apply(Term.ApplyType(termName, List(TypeNames.Int)), List(Lit.Int(3)))

    when(scalaCollectionNameClassifier.isCollection(eqTree(termName))).thenReturn(true)

    termApplyClassifier.isCollectionInitializer(termApply) shouldBe true
  }

  test("isCollectionInitializer() when method name is not a collection - should return false") {
    val termName = Term.Name("foo")
    val termApply = Term.Apply(termName, List(Lit.Int(3)))

    when(scalaCollectionNameClassifier.isCollection(eqTree(termName))).thenReturn(false)

    termApplyClassifier.isCollectionInitializer(termApply) shouldBe false
  }
}
