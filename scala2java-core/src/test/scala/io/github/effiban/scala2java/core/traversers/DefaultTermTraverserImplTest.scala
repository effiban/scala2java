package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, IfContext, TryContext}
import io.github.effiban.scala2java.core.entities.Decision.No
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.SingleTermFunctionTraversalResult
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Term.AnonymousFunction
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
  private val defaultBlockTraverser = mock[DefaultBlockTraverser]
  private val defaultIfTraverser = mock[DefaultIfTraverser]
  private val termMatchTraverser = mock[TermMatchTraverser]
  private val tryTraverser = mock[TryTraverser]
  private val tryWithHandlerTraverser = mock[TryWithHandlerTraverser]
  private val termFunctionTraverser = mock[TermFunctionTraverser]
  private val partialFunctionTraverser = mock[PartialFunctionTraverser]
  private val anonymousFunctionTraverser = mock[AnonymousFunctionTraverser]
  private val whileTraverser = mock[WhileTraverser]

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
    termTupleTraverser,
    defaultBlockTraverser,
    defaultIfTraverser,
    termMatchTraverser,
    tryTraverser,
    tryWithHandlerTraverser,
    termFunctionTraverser,
    partialFunctionTraverser,
    anonymousFunctionTraverser,
    whileTraverser
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

  test("traverse() for Block") {
    val block =
      q"""
      {
        stat1
        stat2
      }
      """

    val traversedBlock =
      q"""
      {
        traversedStat1
        traversedStat2
      }
      """

    doReturn(TestableBlockTraversalResult(traversedBlock)).when(defaultBlockTraverser).traverse(eqTree(block), eqBlockContext(BlockContext()))

    defaultTermTraverser.traverse(block).structure shouldBe traversedBlock.structure
  }

  test("traverse() for Term.If") {
    val `if` = q"if (x < 3) doA() else doB()"
    val traversedIf =
      q"""
      if (x < 33) {
        doAA()
      } else {
        doBB()
      }
      """

    doReturn(TestableIfTraversalResult(traversedIf)).when(defaultIfTraverser).traverse(eqTree(`if`), eqTo(IfContext()))

    defaultTermTraverser.traverse(`if`).structure shouldBe traversedIf.structure
  }

  test("traverse() for Term.Match") {
    val termMatch =
      q"""
      x match {
        case 1 => "one"
        case 2 => "two"
        case _ => "many"
      }
      """

    val traversedTermMatch =
      q"""
      xx match {
        case 10 => "ten"
        case 20 => "twenty"
        case _ => "many"
      }
      """

    doReturn(traversedTermMatch).when(termMatchTraverser).traverse(eqTree(termMatch))

    defaultTermTraverser.traverse(termMatch).structure shouldBe traversedTermMatch.structure
  }

  test("traverse() for Term.Try") {
    val termTry = q"try(doSomething) catch { case e: Exception1 => log(error) }"

    val traversedTermTry =
      q"""
      try {
        doSomething
      } catch {
        case ee: Exception11 => {
          log(error)
        }
      }
      """

    doReturn(TestableTryTraversalResult(traversedTermTry, catchUncertainReturns = List(false)))
      .when(tryTraverser).traverse(eqTree(termTry), eqTo(TryContext()))

    defaultTermTraverser.traverse(termTry).structure shouldBe traversedTermTry.structure
  }

  test("traverse() for Term.TryWithHandler") {
    val tryWithHandler = q"try(doSomething) catch(catchHandler)"

    val traversedTryWithHandler =
      q"""
      try {
        doSomething2
      } catch(catchHandler)
      """

    doReturn(TestableTryWithHandlerTraversalResult(traversedTryWithHandler))
      .when(tryWithHandlerTraverser).traverse(eqTree(tryWithHandler), eqTo(TryContext()))

    defaultTermTraverser.traverse(tryWithHandler).structure shouldBe traversedTryWithHandler.structure
  }

  test("traverse() for Term.Function") {
    val termFunction = q"arg => doSomething(arg)"
    val traversedTermFunction = q"traversedArg => doSomethingElse(traversedArg)"

    doReturn(SingleTermFunctionTraversalResult(traversedTermFunction))
      .when(termFunctionTraverser).traverse(eqTree(termFunction), eqTo(No))

    defaultTermTraverser.traverse(termFunction).structure shouldBe traversedTermFunction.structure
  }

  test("traverse() for Term.PartialFunction") {
    val partialFunction =
      q"""
      {
        case 1 => "one"
        case 2 => "two"
      }
      """

    val traversedTermFunction =
      q"""
      arg => arg match {
        case 10 => "ten"
        case 20 => "twenty"
      }
      """

    doReturn(SingleTermFunctionTraversalResult(traversedTermFunction))
      .when(partialFunctionTraverser).traverse(eqTree(partialFunction))

    defaultTermTraverser.traverse(partialFunction).structure shouldBe traversedTermFunction.structure
  }

  test("traverse() for Term.AnonymousFunction") {
    val anonFunction = AnonymousFunction(q"doSomething()")
    val traversedFunction = q"__ => doSomethingElse()"

    doReturn(SingleTermFunctionTraversalResult(traversedFunction))
      .when(anonymousFunctionTraverser).traverse(eqTree(anonFunction), eqTo(No))

    defaultTermTraverser.traverse(anonFunction).structure shouldBe traversedFunction.structure
  }

  test("traverse() for While") {
    val `while` = q"while (x < 3) doIt(x)"
    val traversedWhile =
      q"""
      while (xx < 33) {
        doooIttt(xx)
      }
      """

    doReturn(traversedWhile).when(whileTraverser).traverse(eqTree(`while`))

    defaultTermTraverser.traverse(`while`).structure shouldBe traversedWhile.structure
  }

  test("traverse() for Term.Placeholder") {
    defaultTermTraverser.traverse(Term.Placeholder()).structure shouldBe Term.Placeholder().structure
  }

  test("traverse() for Lit.Int") {
    val lit = Lit.Int(3)
    defaultTermTraverser.traverse(lit).structure shouldBe lit.structure
  }
}
