package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.IfContext
import io.github.effiban.scala2java.core.entities.Decision.{Decision, No, Uncertain, Yes}
import io.github.effiban.scala2java.core.resolvers.ShouldReturnValueResolver
import io.github.effiban.scala2java.core.traversers.results.BlockStatTraversalResult

import scala.meta.Term.{If, Return, Try, TryWithHandler}
import scala.meta.{Stat, Term}

trait BlockLastStatTraverser {

  def traverse(stat: Stat, shouldReturnValue: Decision = No): BlockStatTraversalResult
}

private[traversers] class BlockLastStatTraverserImpl(blockStatTraverser: => BlockStatTraverser,
                                                     defaultIfTraverser: => DefaultIfTraverser,
                                                     shouldReturnValueResolver: => ShouldReturnValueResolver)
  extends BlockLastStatTraverser {

  override def traverse(stat: Stat, shouldReturnValue: Decision = No): BlockStatTraversalResult = {
    stat match {
      case `if`: If => traverseIf(`if`, shouldReturnValue)
      case `try`: Try => BlockStatTraversalResult(`try`) // TODO
      case tryWithHandler: TryWithHandler => BlockStatTraversalResult(tryWithHandler) //TODO
      case term: Term => traverseTerm(term, shouldReturnValue)
      case aStat => BlockStatTraversalResult(traverseInner(aStat))
    }
  }

  private def traverseIf(`if`: If, shouldReturnValue: Decision) = {
    val theIfResult = defaultIfTraverser.traverse(`if`, IfContext(shouldReturnValue))
    BlockStatTraversalResult(theIfResult.`if`, uncertainReturn = theIfResult.uncertainReturn)
  }

  private def traverseTerm(term: Term, shouldReturnValue: Decision): BlockStatTraversalResult = {
    val shouldTermReturnValue = shouldReturnValueResolver.resolve(term, shouldReturnValue)
    shouldTermReturnValue match {
      case Yes => BlockStatTraversalResult(traverseInner(Return(term)))
      case Uncertain => BlockStatTraversalResult(traverseInner(term), uncertainReturn = true)
      case No => BlockStatTraversalResult(traverseInner(term))
    }
  }

  private def traverseInner(stat: Stat) = blockStatTraverser.traverse(stat)
}
