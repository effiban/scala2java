package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class SuperTypeInferrerImplTest extends UnitTestSuite {

  private val innermostEnclosingTemplateAncestorsInferrer = mock[InnermostEnclosingTemplateAncestorsInferrer]

  private val superTypeInferrer = new SuperTypeInferrerImpl(innermostEnclosingTemplateAncestorsInferrer)

  test("infer when has no 'thisp' and has 'superp', and one parent of enclosing type which matches") {
    val theSuper = q"super[A]"
    val parent = t"A"

    when(innermostEnclosingTemplateAncestorsInferrer.infer(eqTree(theSuper), eqTo(None))).thenReturn(List(parent))

    superTypeInferrer.infer(theSuper).value.structure shouldBe parent.structure
  }

  test("infer when has no 'thisp' and has 'superp', and two ancestors of enclosing type, one matching") {
    val theSuper = q"super[B]"
    val ancestorA = t"A"
    val ancestorB = t"B"

    when(innermostEnclosingTemplateAncestorsInferrer.infer(eqTree(theSuper), eqTo(None))).thenReturn(List(ancestorA, ancestorB))

    superTypeInferrer.infer(theSuper).value.structure shouldBe ancestorB.structure
  }

  test("infer when has no 'thisp' and has 'superp', and one ancestor of enclosing type, which doesn't match") {
    val theSuper = q"super[A]"
    val ancestorB = t"B"

    when(innermostEnclosingTemplateAncestorsInferrer.infer(eqTree(theSuper), eqTo(None))).thenReturn(List(ancestorB))

    superTypeInferrer.infer(theSuper) shouldBe None
  }

  test("infer when has no 'thisp' and has 'superp', and no ancestors of enclosing type") {
    val theSuper = q"super[A]"

    when(innermostEnclosingTemplateAncestorsInferrer.infer(eqTree(theSuper), eqTo(None))).thenReturn(Nil)

    superTypeInferrer.infer(theSuper) shouldBe None
  }

  test("infer when has a 'thisp' and has no 'superp', and has one parent of enclosing type") {
    val theSuper = q"A.super"
    val parent = t"B"

    when(innermostEnclosingTemplateAncestorsInferrer.infer(eqTree(theSuper), eqTo(Some("A")))).thenReturn(List(parent))

    superTypeInferrer.infer(theSuper).value.structure shouldBe parent.structure
  }

  test("infer when has a 'thisp' and has no 'superp', and has two ancestors of enclosing type - should return first") {
    val theSuper = q"A.super"
    val ancestorB = t"B"
    val ancestorC = t"C"

    when(innermostEnclosingTemplateAncestorsInferrer.infer(eqTree(theSuper), eqTo(Some("A")))).thenReturn(List(ancestorB, ancestorC))

    superTypeInferrer.infer(theSuper).value.structure shouldBe ancestorB.structure
  }

  test("infer when has a 'thisp' and has no 'superp', and has no ancestors of enclosing type") {
    val theSuper = q"A.super"

    when(innermostEnclosingTemplateAncestorsInferrer.infer(eqTree(theSuper), eqTo(Some("A")))).thenReturn(Nil)

    superTypeInferrer.infer(theSuper) shouldBe None
  }

  test("infer when has a 'thisp' and has a 'superp', and one parent of enclosing type which matches") {
    val theSuper = q"A.super[B]"
    val parent = t"B"

    when(innermostEnclosingTemplateAncestorsInferrer.infer(eqTree(theSuper), eqTo(Some("A")))).thenReturn(List(parent))

    superTypeInferrer.infer(theSuper).value.structure shouldBe parent.structure
  }

  test("infer when has a 'thisp' and has a 'superp', and two ancestors of enclosing type, one matching") {
    val theSuper = q"A.super[C]"
    val ancestorB = t"B"
    val ancestorC = t"C"

    when(innermostEnclosingTemplateAncestorsInferrer.infer(eqTree(theSuper), eqTo(Some("A")))).thenReturn(List(ancestorB, ancestorC))

    superTypeInferrer.infer(theSuper).value.structure shouldBe ancestorC.structure
  }

  test("infer when has a 'thisp' and has a 'superp', and one parent of enclosing type which doesn't match") {
    val theSuper = q"A.super[B]"
    val parent = t"C"

    when(innermostEnclosingTemplateAncestorsInferrer.infer(eqTree(theSuper), eqTo(Some("A")))).thenReturn(List(parent))

    superTypeInferrer.infer(theSuper) shouldBe None
  }

  test("infer when has a 'thisp' and has a 'superp', and no ancestors of enclosing type") {
    val theSuper = q"A.super[B]"

    when(innermostEnclosingTemplateAncestorsInferrer.infer(eqTree(theSuper), eqTo(Some("A")))).thenReturn(Nil)

    superTypeInferrer.infer(theSuper) shouldBe None
  }
}

