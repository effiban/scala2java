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

class BlockStatTraverserImplTest extends UnitTestSuite {

  private val TheTermName = q"foo"
  private val TheTraversedTermName = q"traversedFoo"
  
  private val TheTermApply = q"foo()"
  private val TheTraversedTermApply = q"traversedFoo()"
  
  private val expressionTermRefTraverser = mock[ExpressionTermRefTraverser]
  private val defaultTermTraverser = mock[DefaultTermTraverser]
  private val shouldReturnValueResolver = mock[ShouldReturnValueResolver]

  private val blockStatTraverser = new BlockStatTraverserImpl(
    expressionTermRefTraverser,
    defaultTermTraverser,
    shouldReturnValueResolver
  )


  test("traverse() for a Term.Name") {
    doReturn(TheTraversedTermName).when(expressionTermRefTraverser).traverse(eqTree(TheTermName))

    blockStatTraverser.traverse(TheTermName).structure shouldBe TheTraversedTermName.structure
  }

  test("traverse() for a Term.Apply") {
    doReturn(TheTraversedTermApply).when(defaultTermTraverser).traverse(eqTree(TheTermApply))

    blockStatTraverser.traverse(TheTermApply).structure shouldBe TheTraversedTermApply.structure
  }
  
  test("traverseLast() for a 'Term.Apply' when shouldReturnValue=Yes and shouldTermReturnValue=Yes") {
    val expectedTraversalResult = BlockStatTraversalResult(Return(TheTraversedTermApply))
    
    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), eqTo(Yes))).thenReturn(Yes)
    doReturn(Return(TheTraversedTermApply)).when(defaultTermTraverser).traverse(eqTree(Return(TheTermApply)))

    blockStatTraverser.traverseLast(TheTermApply, shouldReturnValue = Yes) should equalBlockStatTraversalResult(expectedTraversalResult)
  }

  test("traverseLast() for a 'Term.Apply' when shouldReturnValue=Yes and shouldTermReturnValue=No") {
    val expectedTraversalResult = BlockStatTraversalResult(TheTraversedTermApply)

    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), eqTo(Yes))).thenReturn(No)
    doReturn(TheTraversedTermApply).when(defaultTermTraverser).traverse(eqTree(TheTermApply))

    blockStatTraverser.traverseLast(TheTermApply, shouldReturnValue = Yes) should equalBlockStatTraversalResult(expectedTraversalResult)
  }

  test("traverseLast() for a 'Term.Apply' when shouldReturnValue=Uncertain and shouldTermReturnValue=Uncertain") {
    val expectedTraversalResult = BlockStatTraversalResult(TheTraversedTermApply, uncertainReturn = true)

    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), eqTo(Uncertain))).thenReturn(Uncertain)
    doReturn(TheTraversedTermApply).when(defaultTermTraverser).traverse(eqTree(TheTermApply))

    blockStatTraverser.traverseLast(TheTermApply, shouldReturnValue = Uncertain) should
      equalBlockStatTraversalResult(expectedTraversalResult)
  }

  test("traverseLast() for a 'Term.Apply' when shouldReturnValue=No and shouldTermReturnValue=No") {
    val expectedTraversalResult = BlockStatTraversalResult(TheTraversedTermApply)

    when(shouldReturnValueResolver.resolve(eqTree(TheTermApply), eqTo(No))).thenReturn(No)
    doReturn(TheTraversedTermApply).when(defaultTermTraverser).traverse(eqTree(TheTermApply))

    blockStatTraverser.traverseLast(TheTermApply, shouldReturnValue = No) should equalBlockStatTraversalResult(expectedTraversalResult)
  }
}
