package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, StatContext}
import io.github.effiban.scala2java.core.entities.Decision.{Decision, No}
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.Term
import scala.meta.Term.Block

trait TermFunctionTraverser {
  def traverse(function: Term.Function, shouldBodyReturnValue: Decision = No): Term.Function
}

private[traversers] class TermFunctionTraverserImpl(termParamTraverser: => TermParamTraverser,
                                                    defaultBlockTraverser: => DefaultBlockTraverser,
                                                    defaultTermTraverser: => DefaultTermTraverser) extends TermFunctionTraverser {

  // lambda definition
  override def traverse(function: Term.Function, shouldBodyReturnValue: Decision = No): Term.Function = {
    val paramContext = StatContext(JavaScope.LambdaSignature)
    val traversedParams = function.params.map(param => termParamTraverser.traverse(param, paramContext))

    function.body match {
      // Block of size 2 or more
      case block@Block(_ :: _ :: _) => traverseBlockBody(shouldBodyReturnValue, traversedParams, block)
      // Block of size 1 with a Term - treat as a plain term for nicer style, because Java does support a single-term lambda
      case Block(List(term: Term)) => traverseSingleTermBody(traversedParams, term)
      // Block of size 1 with a non-Term - must treat as a Block because a Java lambda body must be a Term
      case block: Block => traverseBlockBody(shouldBodyReturnValue, traversedParams, block)
      case term => traverseSingleTermBody(traversedParams, term)
    }
  }

  private def traverseBlockBody(shouldBodyReturnValue: Decision,
                                traversedParams: List[Term.Param],
                                blockBody: Block) = {
    val traversedBody = defaultBlockTraverser.traverse(blockBody, context = BlockContext(shouldReturnValue = shouldBodyReturnValue))
    Term.Function(traversedParams, traversedBody)
  }

  private def traverseSingleTermBody(traversedParams: List[Term.Param], term: Term) = {
    val traversedTerm = defaultTermTraverser.traverse(term)
    Term.Function(traversedParams, traversedTerm)
  }
}
