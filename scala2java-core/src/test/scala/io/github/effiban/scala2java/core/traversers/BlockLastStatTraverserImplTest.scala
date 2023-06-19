package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.Decision.{No, Uncertain, Yes}
import io.github.effiban.scala2java.core.matchers.BlockStatTraversalResultScalatestMatcher.equalBlockStatTraversalResult
import io.github.effiban.scala2java.core.resolvers.ShouldReturnValueResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.BlockStatTraversalResult
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Term.Return
import scala.meta.XtensionQuasiquoteTerm

class BlockLastStatTraverserImplTest extends UnitTestSuite {

  private val TheTermApply = q"foo()"
  private val TheTraversedTermApply = q"traversedFoo()"

  private val blockStatTraverser = mock[BlockStatTraverser]
  private val shouldReturnValueResolver = mock[ShouldReturnValueResolver]

  private val blockLastStatTraverser = new BlockLastStatTraverserImpl(
    blockStatTraverser,
    shouldReturnValueResolver
  )


  test("traverse() for a 'Term.Apply' when shouldReturnValue=Yes and shouldTermReturnValue=Yes") {
    val expectedTraversalResult = BlockStatTraversalResult(Return(TheTraversedTermApply))

    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), eqTo(Yes))).thenReturn(Yes)
    doReturn(Return(TheTraversedTermApply)).when(blockStatTraverser).traverse(eqTree(Return(TheTermApply)))

    blockLastStatTraverser.traverse(TheTermApply, shouldReturnValue = Yes) should equalBlockStatTraversalResult(expectedTraversalResult)
  }

  test("traverse() for a 'Term.Apply' when shouldReturnValue=Yes and shouldTermReturnValue=No") {
    val expectedTraversalResult = BlockStatTraversalResult(TheTraversedTermApply)

    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), eqTo(Yes))).thenReturn(No)
    doReturn(TheTraversedTermApply).when(blockStatTraverser).traverse(eqTree(TheTermApply))

    blockLastStatTraverser.traverse(TheTermApply, shouldReturnValue = Yes) should equalBlockStatTraversalResult(expectedTraversalResult)
  }

  test("traverse() for a 'Term.Apply' when shouldReturnValue=Uncertain and shouldTermReturnValue=Uncertain") {
    val expectedTraversalResult = BlockStatTraversalResult(TheTraversedTermApply, uncertainReturn = true)

    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), eqTo(Uncertain))).thenReturn(Uncertain)
    doReturn(TheTraversedTermApply).when(blockStatTraverser).traverse(eqTree(TheTermApply))

    blockLastStatTraverser.traverse(TheTermApply, shouldReturnValue = Uncertain) should
      equalBlockStatTraversalResult(expectedTraversalResult)
  }

  test("traverse() for a 'Term.Apply' when shouldReturnValue=No and shouldTermReturnValue=No") {
    val expectedTraversalResult = BlockStatTraversalResult(TheTraversedTermApply)

    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), eqTo(No))).thenReturn(No)
    doReturn(TheTraversedTermApply).when(blockStatTraverser).traverse(eqTree(TheTermApply))

    blockLastStatTraverser.traverse(TheTermApply, shouldReturnValue = No) should equalBlockStatTraversalResult(expectedTraversalResult)
  }

  test("traverse() for a 'Defn.Val'") {
    val defnVal = q"val x = 3"
    val traversedDefnVal = q"val x = 33"
    val expectedTraversalResult = BlockStatTraversalResult(traversedDefnVal)

    doReturn(traversedDefnVal).when(blockStatTraverser).traverse(eqTree(defnVal))

    blockLastStatTraverser.traverse(defnVal) should equalBlockStatTraversalResult(expectedTraversalResult)
  }
}
