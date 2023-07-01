package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, StatContext}
import io.github.effiban.scala2java.core.entities.Decision.{Decision, No}
import io.github.effiban.scala2java.core.traversers.results.{BlockTermFunctionTraversalResult, SingleTermFunctionTraversalResult, TermFunctionTraversalResult}
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.Term
import scala.meta.Term.Block

trait TermFunctionTraverser {
  def traverse(function: Term.Function, shouldBodyReturnValue: Decision = No): TermFunctionTraversalResult
}

private[traversers] class TermFunctionTraverserImpl(termParamTraverser: => TermParamTraverser,
                                                    defaultBlockTraverser: => DefaultBlockTraverser,
                                                    defaultTermTraverser: => DefaultTermTraverser) extends TermFunctionTraverser {

  // lambda definition
  override def traverse(function: Term.Function, shouldBodyReturnValue: Decision = No): TermFunctionTraversalResult = {
    val paramContext = StatContext(JavaScope.LambdaSignature)
    val traversedParams = function.params
      .map(param => termParamTraverser.traverse(param, paramContext))
      // Ignoring the returned Java modifiers here, since there cannot be any Java modifiers for a lambda param
      .map(_.tree)

    function.body match {
      case block: Block => traverseBlockBody(shouldBodyReturnValue, traversedParams, block)
      case bodyTerm => traverseSingleTermBody(traversedParams, bodyTerm)
    }
  }

  private def traverseBlockBody(shouldBodyReturnValue: Decision,
                                traversedParams: List[Term.Param],
                                blockBody: Block) = {
    val bodyResult = defaultBlockTraverser.traverse(blockBody, context = BlockContext(shouldReturnValue = shouldBodyReturnValue))
    BlockTermFunctionTraversalResult(traversedParams, bodyResult)
  }

  private def traverseSingleTermBody(traversedParams: List[Term.Param], bodyTerm: Term) = {
    val traversedTerm = defaultTermTraverser.traverse(bodyTerm)
    SingleTermFunctionTraversalResult(Term.Function(traversedParams, traversedTerm))
  }
}
