package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.Decision.{Decision, Uncertain}
import io.github.effiban.scala2java.core.entities.TraversalConstants.JavaPlaceholder

import scala.meta.Term
import scala.meta.Term.{AnonymousFunction, Param}

@deprecated
trait DeprecatedAnonymousFunctionTraverser {
  def traverse(anonymousFunction: AnonymousFunction, shouldBodyReturnValue: Decision = Uncertain): Unit
}

@deprecated
private[traversers] class DeprecatedAnonymousFunctionTraverserImpl(termFunctionTraverser: => DeprecatedTermFunctionTraverser) extends DeprecatedAnonymousFunctionTraverser {

  override def traverse(anonymousFunction: AnonymousFunction, shouldBodyReturnValue: Decision = Uncertain): Unit = {
    val function = Term.Function(
      params = List(Param(name = Term.Name(JavaPlaceholder), mods = Nil, decltpe = None, default = None)),
      body = anonymousFunction.body)

    termFunctionTraverser.traverse(function, shouldBodyReturnValue)
  }
}
