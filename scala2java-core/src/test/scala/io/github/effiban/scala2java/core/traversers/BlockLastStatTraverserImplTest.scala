package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.IfContext
import io.github.effiban.scala2java.core.entities.Decision.{No, Uncertain, Yes}
import io.github.effiban.scala2java.core.matchers.BlockStatTraversalResultScalatestMatcher.equalBlockStatTraversalResult
import io.github.effiban.scala2java.core.resolvers.ShouldReturnValueResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{BlockStatTraversalResult, IfTraversalResult}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Term.Return
import scala.meta.XtensionQuasiquoteTerm

class BlockLastStatTraverserImplTest extends UnitTestSuite {

  private val TheIf = q"if (x < 3) small else large"
  private val TheTraversedIf = q"if (x < 0.3) verySmall else veryLarge"

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
    val expectedIfTraversalResult = IfTraversalResult(TheTraversedIf)
    val expectedBlockTraversalResult = BlockStatTraversalResult(TheTraversedIf)

    doReturn(expectedIfTraversalResult).when(defaultIfTraverser).traverse(eqTree(TheIf), eqTo(IfContext()))

    blockLastStatTraverser.traverse(TheIf) should equalBlockStatTraversalResult(expectedBlockTraversalResult)
  }

  test("traverse() for a 'Term.If' when shouldReturnValue=Yes") {
    val expectedIfTraversalResult = IfTraversalResult(TheTraversedIf)
    val expectedBlockTraversalResult = BlockStatTraversalResult(TheTraversedIf)

    doReturn(expectedIfTraversalResult).when(defaultIfTraverser).traverse(eqTree(TheIf), eqTo(IfContext(shouldReturnValue = Yes)))

    blockLastStatTraverser.traverse(TheIf, shouldReturnValue = Yes) should equalBlockStatTraversalResult(expectedBlockTraversalResult)
  }

  test("traverse() for a 'Term.If' when shouldReturnValue=Uncertain and output uncertainReturn=true") {
    val expectedIfTraversalResult = IfTraversalResult(TheTraversedIf, uncertainReturn = true)
    val expectedBlockTraversalResult = BlockStatTraversalResult(TheTraversedIf, uncertainReturn = true)

    doReturn(expectedIfTraversalResult).when(defaultIfTraverser).traverse(eqTree(TheIf), eqTo(IfContext(shouldReturnValue = Uncertain)))

    val actualResult = blockLastStatTraverser.traverse(TheIf, shouldReturnValue = Uncertain)
    actualResult should equalBlockStatTraversalResult(expectedBlockTraversalResult)
  }

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
