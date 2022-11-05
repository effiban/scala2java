package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.classifiers.TermTypeClassifier
import io.github.effiban.scala2java.core.entities.Decision.{No, Uncertain, Yes}
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Term

class ShouldReturnValueResolverImplTest extends UnitTestSuite {

  private val TheTerm = Term.Name("myTerm")

  private val termTypeClassifier = mock[TermTypeClassifier]

  private val shouldReturnValueResolver = new ShouldReturnValueResolverImpl(termTypeClassifier)

  test("resolve() when parentShouldReturnValue='Yes' and term returnable='Yes' should return 'Yes'") {
    when(termTypeClassifier.isReturnable(eqTree(TheTerm))).thenReturn(Yes)
    shouldReturnValueResolver.resolve(TheTerm, Yes) shouldBe Yes
  }

  test("resolve() when parentShouldReturnValue='Yes' and term returnable='Uncertain' should return 'Uncertain'") {
    when(termTypeClassifier.isReturnable(eqTree(TheTerm))).thenReturn(Uncertain)
    shouldReturnValueResolver.resolve(TheTerm, Yes) shouldBe Yes
  }

  test("resolve() when parentShouldReturnValue='Yes' and term returnable='No' should return 'No'") {
    when(termTypeClassifier.isReturnable(eqTree(TheTerm))).thenReturn(No)
    shouldReturnValueResolver.resolve(TheTerm, Yes) shouldBe No
  }

  test("resolve() when parentShouldReturnValue='Uncertain' and term returnable='Yes' should return 'Uncertain'") {
    when(termTypeClassifier.isReturnable(eqTree(TheTerm))).thenReturn(Yes)
    shouldReturnValueResolver.resolve(TheTerm, Uncertain) shouldBe Uncertain
  }

  test("resolve() when parentShouldReturnValue='Uncertain' and term returnable='Uncertain' should return 'Uncertain'") {
    when(termTypeClassifier.isReturnable(eqTree(TheTerm))).thenReturn(Uncertain)
    shouldReturnValueResolver.resolve(TheTerm, Uncertain) shouldBe Uncertain
  }

  test("resolve() when parentShouldReturnValue='Uncertain' and term returnable='No' should return 'No'") {
    when(termTypeClassifier.isReturnable(eqTree(TheTerm))).thenReturn(No)
    shouldReturnValueResolver.resolve(TheTerm, Uncertain) shouldBe No
  }

  test("resolve() when parentShouldReturnValue='No' should return 'No'") {
    shouldReturnValueResolver.resolve(TheTerm, No) shouldBe No
  }
}
