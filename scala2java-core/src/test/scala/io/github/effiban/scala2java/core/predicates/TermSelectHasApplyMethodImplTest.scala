package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.reflection.ScalaReflectionLookup
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class TermSelectHasApplyMethodImplTest extends UnitTestSuite {

  private val scalaReflectionLookup = mock[ScalaReflectionLookup]

  private val termSelectHasApplyMethod = new TermSelectHasApplyMethodImpl(scalaReflectionLookup)

  test(s"When isTermMemberOf() returns true, should return true") {
    val termSelect = q"a.B"
    when(scalaReflectionLookup.isTermMemberOf(eqTree(termSelect), eqTree(q"apply"))).thenReturn(true)
    termSelectHasApplyMethod(termSelect) shouldBe true
  }

  test(s"When isTermMemberOf() returns false, should return false") {
    val termSelect = q"a.B"
    when(scalaReflectionLookup.isTermMemberOf(eqTree(termSelect), eqTree(q"apply"))).thenReturn(false)
    termSelectHasApplyMethod(termSelect) shouldBe false
  }
}
