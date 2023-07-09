package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{IfContext, TryContext}
import io.github.effiban.scala2java.core.entities.Decision.{No, Uncertain, Yes}
import io.github.effiban.scala2java.core.resolvers.ShouldReturnValueResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{TryTraversalResult, TryWithHandlerTraversalResult}
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
    doReturn(TheTraversedIf).when(defaultIfTraverser).traverse(eqTree(TheIf), eqTo(IfContext()))

    blockLastStatTraverser.traverse(TheIf).structure shouldBe TheTraversedIf.structure
  }

  test("traverse() for a 'Term.If' when shouldReturnValue=Yes") {
    doReturn(TheTraversedIf).when(defaultIfTraverser).traverse(eqTree(TheIf), eqTo(IfContext(shouldReturnValue = Yes)))

    val actualTraversedIf = blockLastStatTraverser.traverse(TheIf, shouldReturnValue = Yes)
    actualTraversedIf.structure shouldBe TheTraversedIf.structure
  }

  test("traverse() for a 'Term.If' when shouldReturnValue=Uncertain") {
    doReturn(TheTraversedIf).when(defaultIfTraverser).traverse(eqTree(TheIf), eqTo(IfContext(shouldReturnValue = Uncertain)))

    val actualTraversedIf = blockLastStatTraverser.traverse(TheIf, shouldReturnValue = Uncertain)
    actualTraversedIf.structure shouldBe TheTraversedIf.structure
  }

  test("traverse() for a 'Term.Try' when shouldReturnValue=No") {
    val expectedTraversalResult = TryTraversalResult(TheTraversedTry)

    doReturn(expectedTraversalResult).when(tryTraverser).traverse(eqTree(TheTry), eqTo(TryContext()))

    blockLastStatTraverser.traverse(TheTry).structure shouldBe TheTraversedTry.structure
  }

  test("traverse() for a 'Term.Try' when shouldReturnValue=Yes") {
    val expectedTraversalResult = TryTraversalResult(TheTraversedTry)

    doReturn(expectedTraversalResult).when(tryTraverser).traverse(eqTree(TheTry), eqTo(TryContext(shouldReturnValue = Yes)))

    blockLastStatTraverser.traverse(TheTry, shouldReturnValue = Yes).structure shouldBe TheTraversedTry.structure
  }

  test("traverse() for a 'Term.Try' when shouldReturnValue=Uncertain and catch result has uncertainReturn=true") {
    val expectedTraversalResult = TryTraversalResult(TheTraversedTry)

    doReturn(expectedTraversalResult).when(tryTraverser).traverse(eqTree(TheTry), eqTo(TryContext(shouldReturnValue = Uncertain)))

    blockLastStatTraverser.traverse(TheTry, shouldReturnValue = Uncertain).structure shouldBe TheTraversedTry.structure
  }

  test("traverse() for a 'Term.TryWithHandler' when shouldReturnValue=No") {
    val expectedTraversalResult = TryWithHandlerTraversalResult(TheTraversedTryWithHandler)

    doReturn(expectedTraversalResult).when(tryWithHandlerTraverser).traverse(eqTree(TheTryWithHandler), eqTo(TryContext()))

    blockLastStatTraverser.traverse(TheTryWithHandler).structure shouldBe TheTraversedTryWithHandler.structure
  }

  test("traverse() for a 'Term.TryWithHandler' when shouldReturnValue=Yes") {
    val expectedTraversalResult = TryWithHandlerTraversalResult(TheTraversedTryWithHandler)

    doReturn(expectedTraversalResult)
      .when(tryWithHandlerTraverser).traverse(eqTree(TheTryWithHandler), eqTo(TryContext(shouldReturnValue = Yes)))

    val actualTraversedTryWithHandler = blockLastStatTraverser.traverse(TheTryWithHandler, shouldReturnValue = Yes)
    actualTraversedTryWithHandler.structure shouldBe TheTraversedTryWithHandler.structure
  }

  test("traverse() for a 'Term.TryWithHandler' when shouldReturnValue=Uncertain") {
    val expectedTraversalResult = TryWithHandlerTraversalResult(TheTraversedTryWithHandler)

    doReturn(expectedTraversalResult)
      .when(tryWithHandlerTraverser).traverse(eqTree(TheTryWithHandler), eqTo(TryContext(shouldReturnValue = Uncertain)))

    val actualTraversedTryWithHandler = blockLastStatTraverser.traverse(TheTryWithHandler, shouldReturnValue = Uncertain)
    actualTraversedTryWithHandler.structure shouldBe TheTraversedTryWithHandler.structure
  }

  test("traverse() for a 'Term.Apply' when shouldReturnValue=Yes and shouldTermReturnValue=Yes") {
    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), eqTo(Yes))).thenReturn(Yes)
    doReturn(Return(TheTraversedTermApply)).when(blockStatTraverser).traverse(eqTree(Return(TheTermApply)))

    val actualTraversedTermApply = blockLastStatTraverser.traverse(TheTermApply, shouldReturnValue = Yes)
    actualTraversedTermApply.structure shouldBe Return(TheTraversedTermApply).structure
  }

  test("traverse() for a 'Term.Apply' when shouldReturnValue=Yes and shouldTermReturnValue=No") {
    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), eqTo(Yes))).thenReturn(No)
    doReturn(TheTraversedTermApply).when(blockStatTraverser).traverse(eqTree(TheTermApply))

    val actualTraversedTermApply = blockLastStatTraverser.traverse(TheTermApply, shouldReturnValue = Yes)
    actualTraversedTermApply.structure shouldBe TheTraversedTermApply.structure
  }

  test("traverse() for a 'Term.Apply' when shouldReturnValue=Uncertain and shouldTermReturnValue=Uncertain") {
    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), eqTo(Uncertain))).thenReturn(Uncertain)
    doReturn(TheTraversedTermApply).when(blockStatTraverser).traverse(eqTree(TheTermApply))

    val actualTraversedTermApply = blockLastStatTraverser.traverse(TheTermApply, shouldReturnValue = Uncertain)
    actualTraversedTermApply.structure shouldBe TheTraversedTermApply.structure
  }

  test("traverse() for a 'Term.Apply' when shouldReturnValue=No and shouldTermReturnValue=No") {
    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), eqTo(No))).thenReturn(No)
    doReturn(TheTraversedTermApply).when(blockStatTraverser).traverse(eqTree(TheTermApply))

    val actualTraversedTermApply = blockLastStatTraverser.traverse(TheTermApply, shouldReturnValue = No)
    actualTraversedTermApply.structure shouldBe TheTraversedTermApply.structure
  }

  test("traverse() for a 'Defn.Val'") {
    val defnVal = q"val x = 3"
    val traversedDefnVal = q"val x = 33"

    doReturn(traversedDefnVal).when(blockStatTraverser).traverse(eqTree(defnVal))

    blockLastStatTraverser.traverse(defnVal).structure shouldBe traversedDefnVal.structure
  }
}
