package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.IfContext
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

  private val TheTermApply = q"foo()"
  private val TheTraversedTermApply = q"traversedFoo()"

  private val blockStatTraverser = mock[BlockStatTraverser]
  private val defaultIfTraverser = mock[DefaultIfTraverser]
  private val shouldReturnValueResolver = mock[ShouldReturnValueResolver]

  private val blockLastStatTraverser = new BlockLastStatTraverserImpl(
    blockStatTraverser,
    defaultIfTraverser,
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
