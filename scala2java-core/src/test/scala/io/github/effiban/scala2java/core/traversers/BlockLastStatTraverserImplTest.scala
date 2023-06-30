package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{IfContext, TryContext}
import io.github.effiban.scala2java.core.entities.Decision.{No, Uncertain, Yes}
import io.github.effiban.scala2java.core.matchers.BlockStatTraversalResultScalatestMatcher.equalBlockStatTraversalResult
import io.github.effiban.scala2java.core.resolvers.ShouldReturnValueResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.SimpleBlockStatTraversalResult
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Term.Return
import scala.meta.XtensionQuasiquoteTerm

class BlockLastStatTraverserImplTest extends UnitTestSuite {

  private val TheIf = q"if (x < 3) small else large"
  private val TheTraversedIf =
    q"""
    if (x < 0.3) {
      verySmall
    } else {
     veryLarge
    }
    """

  private val TheTry = q"try(doSomething) catch { case e: Exception1 => log(error) }"
  private val TheTraversedTry =
    q"""
    try {
      doSomething
    } catch {
      case ee: Exception11 => {
        log(error)
      }
    }
    """

  private val TheTryWithHandler = q"try(doSomething) catch(catchHandler)"
  private val TheTraversedTryWithHandler =
    q"""
    try {
      doSomething2
    } catch(catchHandler)
    """

  private val TheTermApply = q"foo()"
  private val TheTraversedTermApply = q"traversedFoo()"

  private val blockStatTraverser = mock[BlockStatTraverser]
  private val defaultIfTraverser = mock[DefaultIfTraverser]
  private val tryTraverser = mock[TryTraverser]
  private val tryWithHandlerTraverser = mock[TryWithHandlerTraverser]
  private val shouldReturnValueResolver = mock[ShouldReturnValueResolver]

  private val blockLastStatTraverser = new BlockLastStatTraverserImpl(
    blockStatTraverser,
    defaultIfTraverser,
    tryTraverser,
    tryWithHandlerTraverser,
    shouldReturnValueResolver
  )

  test("traverse() for a 'Term.If' when shouldReturnValue=No") {
    val expectedIfTraversalResult = TestableIfTraversalResult(TheTraversedIf)

    doReturn(expectedIfTraversalResult).when(defaultIfTraverser).traverse(eqTree(TheIf), eqTo(IfContext()))

    blockLastStatTraverser.traverse(TheIf) should equalBlockStatTraversalResult(expectedIfTraversalResult)
  }

  test("traverse() for a 'Term.If' when shouldReturnValue=Yes") {
    val expectedIfTraversalResult = TestableIfTraversalResult(TheTraversedIf)

    doReturn(expectedIfTraversalResult).when(defaultIfTraverser).traverse(eqTree(TheIf), eqTo(IfContext(shouldReturnValue = Yes)))

    val actualTraversalResult = blockLastStatTraverser.traverse(TheIf, shouldReturnValue = Yes)
    actualTraversalResult should equalBlockStatTraversalResult(expectedIfTraversalResult)
  }

  test("traverse() for a 'Term.If' when shouldReturnValue=Uncertain and output uncertainReturn=true") {
    val expectedIfTraversalResult = TestableIfTraversalResult(TheTraversedIf, thenUncertainReturn = true, elseUncertainReturn = true)

    doReturn(expectedIfTraversalResult).when(defaultIfTraverser).traverse(eqTree(TheIf), eqTo(IfContext(shouldReturnValue = Uncertain)))

    val actualTraversalResult = blockLastStatTraverser.traverse(TheIf, shouldReturnValue = Uncertain)
    actualTraversalResult should equalBlockStatTraversalResult(expectedIfTraversalResult)
  }

  test("traverse() for a 'Term.Try' when shouldReturnValue=No") {
    val expectedTraversalResult = TestableTryTraversalResult(TheTraversedTry, catchUncertainReturns = List(false))

    doReturn(expectedTraversalResult).when(tryTraverser).traverse(eqTree(TheTry), eqTo(TryContext()))

    blockLastStatTraverser.traverse(TheTry) should equalBlockStatTraversalResult(expectedTraversalResult)
  }

  test("traverse() for a 'Term.Try' when shouldReturnValue=Yes") {
    val expectedTraversalResult = TestableTryTraversalResult(TheTraversedTry, catchUncertainReturns = List(false))

    doReturn(expectedTraversalResult).when(tryTraverser).traverse(eqTree(TheTry), eqTo(TryContext(shouldReturnValue = Yes)))

    blockLastStatTraverser.traverse(TheTry, shouldReturnValue = Yes) should equalBlockStatTraversalResult(expectedTraversalResult)
  }

  test("traverse() for a 'Term.Try' when shouldReturnValue=Uncertain and catch result has uncertainReturn=true") {
    val expectedTraversalResult = TestableTryTraversalResult(TheTraversedTry, catchUncertainReturns = List(true))

    doReturn(expectedTraversalResult).when(tryTraverser).traverse(eqTree(TheTry), eqTo(TryContext(shouldReturnValue = Uncertain)))

    blockLastStatTraverser.traverse(TheTry, shouldReturnValue = Uncertain) should equalBlockStatTraversalResult(expectedTraversalResult)
  }

  test("traverse() for a 'Term.TryWithHandler' when shouldReturnValue=No") {
    val expectedTraversalResult = TestableTryWithHandlerTraversalResult(TheTraversedTryWithHandler)

    doReturn(expectedTraversalResult).when(tryWithHandlerTraverser).traverse(eqTree(TheTryWithHandler), eqTo(TryContext()))

    blockLastStatTraverser.traverse(TheTryWithHandler) should equalBlockStatTraversalResult(expectedTraversalResult)
  }

  test("traverse() for a 'Term.TryWithHandler' when shouldReturnValue=Yes") {
    val expectedTraversalResult = TestableTryWithHandlerTraversalResult(TheTraversedTryWithHandler)

    doReturn(expectedTraversalResult)
      .when(tryWithHandlerTraverser).traverse(eqTree(TheTryWithHandler), eqTo(TryContext(shouldReturnValue = Yes)))

    blockLastStatTraverser.traverse(TheTryWithHandler, shouldReturnValue = Yes) should
      equalBlockStatTraversalResult(expectedTraversalResult)
  }

  test("traverse() for a 'Term.TryWithHandler' when shouldReturnValue=Uncertain and expr result has uncertainReturn=true") {
    val expectedTraversalResult = TestableTryWithHandlerTraversalResult(TheTraversedTryWithHandler, exprUncertainReturn = true)

    doReturn(expectedTraversalResult)
      .when(tryWithHandlerTraverser).traverse(eqTree(TheTryWithHandler), eqTo(TryContext(shouldReturnValue = Uncertain)))

    blockLastStatTraverser.traverse(TheTryWithHandler, shouldReturnValue = Uncertain) should
      equalBlockStatTraversalResult(expectedTraversalResult)
  }

  test("traverse() for a 'Term.Apply' when shouldReturnValue=Yes and shouldTermReturnValue=Yes") {
    val expectedTraversalResult = SimpleBlockStatTraversalResult(Return(TheTraversedTermApply))

    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), eqTo(Yes))).thenReturn(Yes)
    doReturn(Return(TheTraversedTermApply)).when(blockStatTraverser).traverse(eqTree(Return(TheTermApply)))

    val actualTraversalResult = blockLastStatTraverser.traverse(TheTermApply, shouldReturnValue = Yes)
    actualTraversalResult should equalBlockStatTraversalResult(expectedTraversalResult)
  }

  test("traverse() for a 'Term.Apply' when shouldReturnValue=Yes and shouldTermReturnValue=No") {
    val expectedTraversalResult = SimpleBlockStatTraversalResult(TheTraversedTermApply)

    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), eqTo(Yes))).thenReturn(No)
    doReturn(TheTraversedTermApply).when(blockStatTraverser).traverse(eqTree(TheTermApply))

    val actualTraversalResult = blockLastStatTraverser.traverse(TheTermApply, shouldReturnValue = Yes)
    actualTraversalResult should equalBlockStatTraversalResult(expectedTraversalResult)
  }

  test("traverse() for a 'Term.Apply' when shouldReturnValue=Uncertain and shouldTermReturnValue=Uncertain") {
    val expectedTraversalResult = SimpleBlockStatTraversalResult(TheTraversedTermApply, uncertainReturn = true)

    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), eqTo(Uncertain))).thenReturn(Uncertain)
    doReturn(TheTraversedTermApply).when(blockStatTraverser).traverse(eqTree(TheTermApply))

    val actualTraversalResult = blockLastStatTraverser.traverse(TheTermApply, shouldReturnValue = Uncertain)
    actualTraversalResult should equalBlockStatTraversalResult(expectedTraversalResult)
  }

  test("traverse() for a 'Term.Apply' when shouldReturnValue=No and shouldTermReturnValue=No") {
    val expectedTraversalResult = SimpleBlockStatTraversalResult(TheTraversedTermApply)

    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), eqTo(No))).thenReturn(No)
    doReturn(TheTraversedTermApply).when(blockStatTraverser).traverse(eqTree(TheTermApply))

    val actualTraversalResult = blockLastStatTraverser.traverse(TheTermApply, shouldReturnValue = No)
    actualTraversalResult should equalBlockStatTraversalResult(expectedTraversalResult)
  }

  test("traverse() for a 'Defn.Val'") {
    val defnVal = q"val x = 3"
    val traversedDefnVal = q"val x = 33"
    val expectedTraversalResult = SimpleBlockStatTraversalResult(traversedDefnVal)

    doReturn(traversedDefnVal).when(blockStatTraverser).traverse(eqTree(defnVal))

    blockLastStatTraverser.traverse(defnVal) should equalBlockStatTraversalResult(expectedTraversalResult)
  }
}
