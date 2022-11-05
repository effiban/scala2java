package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.Decision.{Decision, Uncertain}
import io.github.effiban.scala2java.core.entities.TraversalConstants.JavaPlaceholder

import scala.meta.Term
import scala.meta.Term.{AnonymousFunction, Param}

trait AnonymousFunctionTraverser {
  def traverse(anonymousFunction: AnonymousFunction, shouldBodyReturnValue: Decision = Uncertain): Unit
}

private[traversers] class AnonymousFunctionTraverserImpl(termFunctionTraverser: => TermFunctionTraverser) extends AnonymousFunctionTraverser {

  override def traverse(anonymousFunction: AnonymousFunction, shouldBodyReturnValue: Decision = Uncertain): Unit = {
    val function = Term.Function(
      params = List(Param(name = Term.Name(JavaPlaceholder), mods = Nil, decltpe = None, default = None)),
      body = anonymousFunction.body)

    termFunctionTraverser.traverse(function, shouldBodyReturnValue)
  }
}
