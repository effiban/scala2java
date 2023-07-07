package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class StatTermTraverserImplTest extends UnitTestSuite {

  private val expressionTermRefTraverser = mock[ExpressionTermRefTraverser]
  private val defaultTermTraverser = mock[DefaultTermTraverser]

  private val statTermTraverser = new StatTermTraverserImpl(
    expressionTermRefTraverser,
    defaultTermTraverser)

  test("traverse() a Term.Name should call the ExpressionTermRefTraverser") {
    val termName = q"aa"
    val traversedTermName = q"AA"

    doReturn(traversedTermName).when(expressionTermRefTraverser).traverse(eqTree(termName))

    statTermTraverser.traverse(termName).structure shouldBe traversedTermName.structure
  }

  test("traverse() a Term.Select should call the ExpressionTermRefTraverser") {
    val termSelect = q"A.a"
    val traversedTermSelect = q"B.b"

    doReturn(traversedTermSelect).when(expressionTermRefTraverser).traverse(eqTree(termSelect))

    statTermTraverser.traverse(termSelect).structure shouldBe traversedTermSelect.structure
  }

  test("traverse() a Term.ApplyType should call the DefaultTermTraverser") {
    val termApplyType = q"a[Type1]"
    val traversedTermApplyType = q"A[Type2]"

    doReturn(traversedTermApplyType).when(defaultTermTraverser).traverse(termApplyType)

    statTermTraverser.traverse(termApplyType).structure shouldBe traversedTermApplyType.structure
  }

  test("traverse() a Term.Apply should call the DefaultTermTraverser") {
    val termApply = q"a(1)"
    val traversedTermApply = q"A(11)"

    doReturn(traversedTermApply).when(defaultTermTraverser).traverse(eqTree(termApply))

    statTermTraverser.traverse(termApply).structure shouldBe traversedTermApply.structure
  }

  test("traverse() a Term.ApplyInfix should call the DefaultTermTraverser") {
    val termApplyInfix = q"a + b"
    val traversedTermApplyInfix = q"A + B"

    doReturn(traversedTermApplyInfix).when(defaultTermTraverser).traverse(eqTree(termApplyInfix))

    statTermTraverser.traverse(termApplyInfix).structure shouldBe traversedTermApplyInfix.structure
  }
}
