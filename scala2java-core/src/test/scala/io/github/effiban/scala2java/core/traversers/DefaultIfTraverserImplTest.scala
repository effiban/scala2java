package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, IfContext}
import io.github.effiban.scala2java.core.entities.Decision.{Uncertain, Yes}
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.BlockTraversalResult
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.Term.{Block, If}
import scala.meta.{Lit, Term, XtensionQuasiquoteTerm}

class DefaultIfTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[ExpressionTermTraverser]
  private val blockWrappingTermTraverser = mock[BlockWrappingTermTraverser]

  private val ifTraverser = new DefaultIfTraverserImpl(expressionTermTraverser, blockWrappingTermTraverser)

  private val Condition = q"x < 3"
  private val TraversedCondition = q"xx < 3"

  private val ThenTerm = q"operation1(x)"
  private val ThenBlock =
    q"""
    {
      operation1(x)
      operation2(y)
    }
    """
  private val TraversedThenBlock =
    q"""
    {
      traversedOperation1(xx)
      traversedOperation2(yy)
    }
    """

  private val ElseTerm = q"otherOperation1(x)"
  private val ElseBlock =
    q"""
    {
      otherOperation1(x)
      otherOperation2(y)
    }
    """
  private val TraversedElseBlock =
    q"""
    {
      traversedOtherOperation1(xx)
      traversedOtherOperation2(yy)
    }
    """


  test("traverse() when 'then' is a block, no 'else', and shouldReturnValue=No") {
    val `if` = If(
      cond = Condition,
      thenp = ThenBlock,
      elsep = Lit.Unit()
    )

    val traversedIf = If(
      cond = TraversedCondition,
      thenp = TraversedThenBlock,
      elsep = Lit.Unit()
    )

    doReturn(TraversedCondition).when(expressionTermTraverser).traverse(eqTree(Condition))
    doReturn(BlockTraversalResult(TraversedThenBlock))
      .when(blockWrappingTermTraverser).traverse(term = eqTree(ThenBlock), context = eqBlockContext(BlockContext()))

    ifTraverser.traverse(`if`).structure shouldBe traversedIf.structure
  }

  test("traverse() when 'then' is a block, no 'else', and shouldReturnValue=Yes") {
    val `if` = If(
      cond = Condition,
      thenp = ThenBlock,
      elsep = Lit.Unit()
    )

    val traversedIf = If(
      cond = TraversedCondition,
      thenp = TraversedThenBlock,
      elsep = Lit.Unit()
    )


    doReturn(TraversedCondition).when(expressionTermTraverser).traverse(eqTree(Condition))
    doReturn(BlockTraversalResult(TraversedThenBlock)).when(blockWrappingTermTraverser).traverse(
      term = eqTree(ThenBlock),
      context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
    )

    val result = ifTraverser.traverse(`if`, context = IfContext(shouldReturnValue = Yes))
    result.structure shouldBe traversedIf.structure
  }

  test("traverse() when 'then' is a block, no 'else', shouldReturnValue=Uncertain") {
    val `if` = If(
      cond = Condition,
      thenp = ThenBlock,
      elsep = Lit.Unit()
    )

    val traversedIf = If(
      cond = TraversedCondition,
      thenp = TraversedThenBlock,
      elsep = Lit.Unit()
    )


    doReturn(TraversedCondition).when(expressionTermTraverser).traverse(eqTree(Condition))
    doReturn(BlockTraversalResult(TraversedThenBlock)).when(blockWrappingTermTraverser).traverse(
      term = eqTree(ThenBlock),
      context = eqBlockContext(BlockContext(shouldReturnValue = Uncertain))
    )

    val result = ifTraverser.traverse(`if`, context = IfContext(shouldReturnValue = Uncertain))
    result.structure shouldBe traversedIf.structure
  }

  test("traverse() when 'then' is a non-block term, no 'else', and shouldReturnValue=No") {
    val `if` = If(
      cond = Condition,
      thenp = ThenTerm,
      elsep = Lit.Unit()
    )

    val traversedIf = If(
      cond = TraversedCondition,
      thenp = TraversedThenBlock,
      elsep = Lit.Unit()
    )

    doReturn(TraversedCondition).when(expressionTermTraverser).traverse(eqTree(Condition))
    doReturn(BlockTraversalResult(TraversedThenBlock))
      .when(blockWrappingTermTraverser).traverse(term = eqTree(ThenTerm), context = eqBlockContext(BlockContext()))

    ifTraverser.traverse(`if`).structure shouldBe traversedIf.structure
  }

  test("traverse() when 'else' is a block, and shouldReturnValue=No") {
    val `if` = If(
      cond = Condition,
      thenp = ThenTerm,
      elsep = ElseBlock
    )

    val traversedIf = If(
      cond = TraversedCondition,
      thenp = TraversedThenBlock,
      elsep = TraversedElseBlock
    )

    doReturn(TraversedCondition).when(expressionTermTraverser).traverse(eqTree(Condition))
    doAnswer((term: Term, _: BlockContext) => term match {
      case aTerm if aTerm.structure == ThenTerm.structure => BlockTraversalResult(TraversedThenBlock)
      case aTerm if aTerm.structure == ElseBlock.structure => BlockTraversalResult(TraversedElseBlock)
      case _ => BlockTraversalResult(Block(List(Lit.Unit())))
    }).when(blockWrappingTermTraverser).traverse(
      term = any[Term], context = eqBlockContext(BlockContext())
    )

    ifTraverser.traverse(`if`).structure shouldBe traversedIf.structure
  }

  test("traverse() when 'else' is a block, and shouldReturnValue=Yes") {
    val `if` = If(
      cond = Condition,
      thenp = ThenTerm,
      elsep = ElseBlock
    )

    val traversedIf = If(
      cond = TraversedCondition,
      thenp = TraversedThenBlock,
      elsep = TraversedElseBlock
    )

    doReturn(TraversedCondition).when(expressionTermTraverser).traverse(eqTree(Condition))
    doAnswer((term: Term, _: BlockContext) => term match {
      case aTerm if aTerm.structure == ThenTerm.structure => BlockTraversalResult(TraversedThenBlock)
      case aTerm if aTerm.structure == ElseBlock.structure => BlockTraversalResult(TraversedElseBlock)
      case _ => BlockTraversalResult(Block(List(Lit.Unit())))
    }).when(blockWrappingTermTraverser).traverse(
      term = any[Term], context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
    )

    val result = ifTraverser.traverse(`if`, context = IfContext(shouldReturnValue = Yes))
    result.structure shouldBe traversedIf.structure
  }

  test("traverse() when 'else' is a block, shouldReturnValue=Uncertain") {
    val `if` = If(
      cond = Condition,
      thenp = ThenTerm,
      elsep = ElseBlock
    )

    val traversedIf = If(
      cond = TraversedCondition,
      thenp = TraversedThenBlock,
      elsep = TraversedElseBlock
    )

    doReturn(TraversedCondition).when(expressionTermTraverser).traverse(eqTree(Condition))
    doAnswer((term: Term, _: BlockContext) => term match {
      case aTerm if aTerm.structure == ThenTerm.structure => BlockTraversalResult(TraversedThenBlock)
      case aTerm if aTerm.structure == ElseBlock.structure => BlockTraversalResult(TraversedElseBlock)
      case _ => BlockTraversalResult(Block(Nil))
    }).when(blockWrappingTermTraverser).traverse(
      term = any[Term], context = eqBlockContext(BlockContext(shouldReturnValue = Uncertain))
    )


    val actualResult = ifTraverser.traverse(`if`, context = IfContext(shouldReturnValue = Uncertain))
    actualResult.structure shouldBe traversedIf.structure
  }

  test("traverse() when 'else' is a non-block term, and shouldReturnValue=No") {
    val `if` = If(
      cond = Condition,
      thenp = ThenTerm,
      elsep = ElseTerm
    )

    val traversedIf = If(
      cond = TraversedCondition,
      thenp = TraversedThenBlock,
      elsep = TraversedElseBlock
    )

    doReturn(TraversedCondition).when(expressionTermTraverser).traverse(eqTree(Condition))
    doAnswer((term: Term, _: BlockContext) => term match {
      case aTerm if aTerm.structure == ThenTerm.structure => BlockTraversalResult(TraversedThenBlock)
      case aTerm if aTerm.structure == ElseTerm.structure => BlockTraversalResult(TraversedElseBlock)
      case _ => BlockTraversalResult(Block(List(Lit.Unit())))
    }).when(blockWrappingTermTraverser).traverse(
      term = any[Term], context = eqBlockContext(BlockContext())
    )

    ifTraverser.traverse(`if`).structure shouldBe traversedIf.structure
  }
}
