package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Term, XtensionQuasiquoteTerm}

class DefaultTermTraverserImplTest extends UnitTestSuite {

  private val defaultTermRefTraverser = mock[DefaultTermRefTraverser]
  private val termApplyTraverser = mock[TermApplyTraverser]
  private val applyTypeTraverser = mock[ApplyTypeTraverser]
  private val termApplyInfixTraverser = mock[TermApplyInfixTraverser]
  private val assignTraverser = mock[AssignTraverser]
  private val returnTraverser = mock[ReturnTraverser]
  private val throwTraverser = mock[ThrowTraverser]
  private val ascribeTraverser = mock[AscribeTraverser]
  private val termAnnotateTraverser = mock[TermAnnotateTraverser]
  private val termTupleTraverser = mock[TermTupleTraverser]

  private val defaultTermTraverser = new DefaultTermTraverserImpl(
    defaultTermRefTraverser,
    termApplyTraverser,
    applyTypeTraverser,
    termApplyInfixTraverser,
    assignTraverser,
    returnTraverser,
    throwTraverser,
    ascribeTraverser,
    termAnnotateTraverser,
    termTupleTraverser
  )

  test("traverse() for Term.Name") {
    val termName = q"x"
    val traversedTermName = q"traversedX"
    doReturn(traversedTermName).when(defaultTermRefTraverser).traverse(eqTree(termName))

    defaultTermTraverser.traverse(termName).structure shouldBe traversedTermName.structure
  }

  test("traverse() for Term.Apply") {
    val termApply = q"func(2)"
    val traversedTermApply = q"traversedFunc(2)"
    doReturn(traversedTermApply).when(termApplyTraverser).traverse(eqTree(termApply))

    defaultTermTraverser.traverse(termApply).structure shouldBe traversedTermApply.structure
  }

  test("traverse() for Term.ApplyType") {
    val applyType = q"myFunc[MyType]"
    val traversedApplyType = q"myTraversedFunc[MyTraversedType]"

    doReturn(traversedApplyType).when(applyTypeTraverser).traverse(eqTree(applyType))

    defaultTermTraverser.traverse(applyType).structure shouldBe traversedApplyType.structure
  }

  test("traverse() for Term.ApplyInfix") {
    val termApplyInfix = q"a + b"
    val traversedTermApplyInfix = q"aa + bb"
    doReturn(traversedTermApplyInfix).when(termApplyInfixTraverser).traverse(eqTree(termApplyInfix))

    defaultTermTraverser.traverse(termApplyInfix).structure shouldBe traversedTermApplyInfix.structure
  }

  test("traverse() for Term.Assign") {
    val assign = q"x = 3"
    val traversedAssign = q"xx = 33"
    doReturn(traversedAssign).when(assignTraverser).traverse(eqTree(assign))

    defaultTermTraverser.traverse(assign).structure shouldBe traversedAssign.structure
  }

  test("traverse() for Term.Return") {
    val `return` = q"return x"
    val traversedReturn = q"return xx"
    doReturn(traversedReturn).when(returnTraverser).traverse(eqTree(`return`))

    defaultTermTraverser.traverse(`return`).structure shouldBe traversedReturn.structure
  }

  test("traverse() for Term.Throw") {
    val `throw` = q"throw ex"
    val traversedThrow = q"throw ex2"
    doReturn(traversedThrow).when(throwTraverser).traverse(eqTree(`throw`))

    defaultTermTraverser.traverse(`throw`).structure shouldBe traversedThrow.structure
  }

  test("traverse() for Term.Ascribe") {
    val ascribe = q"x: MyType"
    val traversedAscribe = q"xx: MyOtherType"
    doReturn(traversedAscribe).when(ascribeTraverser).traverse(eqTree(ascribe))

    defaultTermTraverser.traverse(ascribe).structure shouldBe traversedAscribe.structure
  }

  test("traverse() for Term.Annotate") {
    val annotate = q"x: @MyAnnot"
    val traversedAnnotate = q"xx: @MyOtherAnnot"
    doReturn(traversedAnnotate).when(termAnnotateTraverser).traverse(eqTree(annotate))

    defaultTermTraverser.traverse(annotate).structure shouldBe traversedAnnotate.structure
  }

  test("traverse() for Term.Tuple") {
    val tuple = q"(x, 1)"
    val traversedTermApply = q"Tuple.tuple(x, 1)"
    doReturn(traversedTermApply).when(termTupleTraverser).traverse(eqTree(tuple))

    defaultTermTraverser.traverse(tuple).structure shouldBe traversedTermApply.structure
  }

  test("traverse() for Term.Placeholder") {
    defaultTermTraverser.traverse(Term.Placeholder()).structure shouldBe Term.Placeholder().structure
  }

  test("traverse() for Lit.Int") {
    val lit = Lit.Int(3)
    defaultTermTraverser.traverse(lit).structure shouldBe lit.structure
  }
}
