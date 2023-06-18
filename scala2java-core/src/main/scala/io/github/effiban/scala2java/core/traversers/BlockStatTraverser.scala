package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.Decision.{Decision, No, Uncertain, Yes}
import io.github.effiban.scala2java.core.resolvers.ShouldReturnValueResolver
import io.github.effiban.scala2java.core.traversers.results.BlockStatTraversalResult

import scala.meta.Term.{If, Return, Try, TryWithHandler}
import scala.meta.{Decl, Defn, Stat, Term}

trait BlockStatTraverser {

  def traverse(stat: Stat): Stat

  def traverseLast(stat: Stat, shouldReturnValue: Decision = No): BlockStatTraversalResult
}

private[traversers] class BlockStatTraverserImpl(expressionTermRefTraverser: => ExpressionTermRefTraverser,
                                                 defaultTermTraverser: => DefaultTermTraverser,
                                                 shouldReturnValueResolver: => ShouldReturnValueResolver) extends BlockStatTraverser {

  override def traverse(stat: Stat): Stat = stat match {
    case termRef: Term.Ref => expressionTermRefTraverser.traverse(termRef)
    case term: Term => defaultTermTraverser.traverse(term)
    case defnVal: Defn.Val => defnVal //TODO
    case defnVar: Defn.Var => defnVar //TODO
    case declVar: Decl.Var => declVar //TODO
    // TODO support other stats once renderers are ready
    case aStat: Stat => throw new UnsupportedOperationException(s"Rendering of $aStat in a block is not supported yet")
  }

  override def traverseLast(stat: Stat, shouldReturnValue: Decision = No): BlockStatTraversalResult = {
    stat match {
      case `if`: If => BlockStatTraversalResult(`if`)// TODO
      case `try`: Try => BlockStatTraversalResult(`try`) // TODO
      case tryWithHandler: TryWithHandler => BlockStatTraversalResult(tryWithHandler) //TODO
      case term: Term => traverseLastTerm(term, shouldReturnValue)
      case _ => BlockStatTraversalResult(traverse(stat))
    }
  }

  private def traverseLastTerm(term: Term, shouldReturnValue: Decision): BlockStatTraversalResult = {
    val shouldTermReturnValue = shouldReturnValueResolver.resolve(term, shouldReturnValue)
    shouldTermReturnValue match {
      case Yes => BlockStatTraversalResult(traverse(Return(term)))
      case Uncertain => BlockStatTraversalResult(traverse(term), uncertainReturn = true)
      case No => BlockStatTraversalResult(traverse(term))
    }
  }
}
