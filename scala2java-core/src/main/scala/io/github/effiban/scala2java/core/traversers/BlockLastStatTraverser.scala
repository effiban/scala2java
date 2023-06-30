package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{IfContext, TryContext}
import io.github.effiban.scala2java.core.entities.Decision.{Decision, No, Uncertain, Yes}
import io.github.effiban.scala2java.core.resolvers.ShouldReturnValueResolver
import io.github.effiban.scala2java.core.traversers.results.{BlockStatTraversalResult, SimpleBlockStatTraversalResult}

import scala.meta.Term.{If, Return, Try, TryWithHandler}
import scala.meta.{Stat, Term}

trait BlockLastStatTraverser {

  def traverse(stat: Stat, shouldReturnValue: Decision = No): BlockStatTraversalResult
}

private[traversers] class BlockLastStatTraverserImpl(blockStatTraverser: => BlockStatTraverser,
                                                     defaultIfTraverser: => DefaultIfTraverser,
                                                     tryTraverser: => TryTraverser,
                                                     shouldReturnValueResolver: => ShouldReturnValueResolver)
  extends BlockLastStatTraverser {

  override def traverse(stat: Stat, shouldReturnValue: Decision = No): BlockStatTraversalResult = {
    stat match {
      case `if`: If => traverseIf(`if`, shouldReturnValue)
      case `try`: Try => traverseTry(`try`, shouldReturnValue)
      case tryWithHandler: TryWithHandler => SimpleBlockStatTraversalResult(tryWithHandler) //TODO
      case term: Term => traverseSimpleTerm(term, shouldReturnValue)
      case aStat => SimpleBlockStatTraversalResult(traverseInner(aStat))
    }
  }

  private def traverseIf(`if`: If, shouldReturnValue: Decision) = {
    defaultIfTraverser.traverse(`if`, IfContext(shouldReturnValue))
  }

  private def traverseTry(`try`: Try, shouldReturnValue: Decision) = {
    tryTraverser.traverse(`try`, TryContext(shouldReturnValue))
  }

  private def traverseSimpleTerm(term: Term, shouldReturnValue: Decision): SimpleBlockStatTraversalResult = {
    val shouldTermReturnValue = shouldReturnValueResolver.resolve(term, shouldReturnValue)
    shouldTermReturnValue match {
      case Yes => SimpleBlockStatTraversalResult(traverseInner(Return(term)))
      case Uncertain => SimpleBlockStatTraversalResult(traverseInner(term), uncertainReturn = true)
      case No => SimpleBlockStatTraversalResult(traverseInner(term))
    }
  }

  private def traverseInner(stat: Stat) = blockStatTraverser.traverse(stat)
}
