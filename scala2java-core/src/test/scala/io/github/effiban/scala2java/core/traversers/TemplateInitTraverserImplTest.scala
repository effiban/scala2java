package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Init, Name, XtensionQuasiquoteInit, XtensionQuasiquoteType}

class TemplateInitTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]

  private val templateInitTraverser = new TemplateInitTraverserImpl(typeTraverser)

  test("traverse when has two non-empty arg lists") {
    val init = init"Parent(1, 2)(3, 4)"
    val expectedTraversedInit = init"TraversedParent()()"

    doReturn(t"TraversedParent").when(typeTraverser).traverse(eqTree(t"Parent"))

    templateInitTraverser.traverse(init).structure shouldBe expectedTraversedInit.structure
  }

  test("traverse when has one non-empty arg list and one empty") {
    val init = init"Parent(1, 2)()"
    val expectedTraversedInit = init"TraversedParent()()"

    doReturn(t"TraversedParent").when(typeTraverser).traverse(eqTree(t"Parent"))

    templateInitTraverser.traverse(init).structure shouldBe expectedTraversedInit.structure
  }

  test("traverse when has two empty arg lists") {
    val init = init"Parent()()"
    val expectedTraversedInit = init"TraversedParent()()"

    doReturn(t"TraversedParent").when(typeTraverser).traverse(eqTree(t"Parent"))

    templateInitTraverser.traverse(init).structure shouldBe expectedTraversedInit.structure
  }

  test("traverse when has one non-empty arg list") {
    val init = init"Parent(1, 2)"
    val expectedTraversedInit = init"TraversedParent()"

    doReturn(t"TraversedParent").when(typeTraverser).traverse(eqTree(t"Parent"))

    templateInitTraverser.traverse(init).structure shouldBe expectedTraversedInit.structure
  }

  test("traverse when has one empty arg list") {
    val init = init"Parent()"
    val expectedTraversedInit = init"TraversedParent()"

    doReturn(t"TraversedParent").when(typeTraverser).traverse(eqTree(t"Parent"))

    templateInitTraverser.traverse(init).structure shouldBe expectedTraversedInit.structure
  }

  test("traverse when has no arg lists") {
    val init = Init(t"Parent", Name.Anonymous(), Nil)
    val expectedTraversedInit = Init(t"TraversedParent", Name.Anonymous(), Nil)

    doReturn(t"TraversedParent").when(typeTraverser).traverse(eqTree(t"Parent"))

    templateInitTraverser.traverse(init).structure shouldBe expectedTraversedInit.structure
  }
}
