package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{IfContext, TryContext}
import io.github.effiban.scala2java.core.entities.Decision.{Decision, No, Yes}
import io.github.effiban.scala2java.core.resolvers.ShouldReturnValueResolver

import scala.meta.Term.{If, Return, Try, TryWithHandler}
import scala.meta.{Stat, Term}

trait BlockLastStatTraverser {

  def traverse(stat: Stat, shouldReturnValue: Decision = No): Stat
}

private[traversers] class BlockLastStatTraverserImpl(blockStatTraverser: => BlockStatTraverser,
                                                     defaultIfTraverser: => DefaultIfTraverser,
                                                     tryTraverser: => TryTraverser,
                                                     tryWithHandlerTraverser: => TryWithHandlerTraverser,
                                                     shouldReturnValueResolver: => ShouldReturnValueResolver)
  extends BlockLastStatTraverser {

  override def traverse(stat: Stat, shouldReturnValue: Decision = No): Stat = {
    stat match {
      case `if`: If => traverseIf(`if`, shouldReturnValue)
      case `try`: Try => traverseTry(`try`, shouldReturnValue).stat
      case tryWithHandler: TryWithHandler => traverseTryWithHandler(tryWithHandler, shouldReturnValue).stat
      case term: Term => traverseSimpleTerm(term, shouldReturnValue)
      case aStat => traverseInner(aStat)
    }
  }

  private def traverseIf(`if`: If, shouldReturnValue: Decision) = {
    defaultIfTraverser.traverse(`if`, IfContext(shouldReturnValue))
  }

  private def traverseTry(`try`: Try, shouldReturnValue: Decision) = {
    tryTraverser.traverse(`try`, TryContext(shouldReturnValue))
  }

  private def traverseTryWithHandler(tryWithHandler: TryWithHandler, shouldReturnValue: Decision) = {
    tryWithHandlerTraverser.traverse(tryWithHandler, TryContext(shouldReturnValue))
  }

  private def traverseSimpleTerm(term: Term, shouldReturnValue: Decision): Stat = {
    val shouldTermReturnValue = shouldReturnValueResolver.resolve(term, shouldReturnValue)
    shouldTermReturnValue match {
      case Yes => traverseInner(Return(term))
      case _ => traverseInner(term)
    }
  }

  private def traverseInner(stat: Stat) = blockStatTraverser.traverse(stat)
}
