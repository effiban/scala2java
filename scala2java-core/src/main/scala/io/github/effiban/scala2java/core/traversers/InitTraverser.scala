package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentListContext, InitContext}
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions

import scala.meta.{Init, Term}

trait InitTraverser {
  def traverse(init: Init, context: InitContext = InitContext()): Unit
}

private[traversers] class InitTraverserImpl(typeTraverser: => TypeTraverser,
                                            argumentListTraverser: => ArgumentListTraverser,
                                            invocationArgTraverser: => ArgumentTraverser[Term]) extends InitTraverser {

  // An 'Init' is an instantiated type, such as with `new` or as a parent in a type definition
  override def traverse(init: Init, context: InitContext = InitContext()): Unit = {
    typeTraverser.traverse(init.tpe)

    if (!context.ignoreArgs) {
      val options = ListTraversalOptions(traverseEmpty = context.traverseEmpty, maybeEnclosingDelimiter = Some(Parentheses))
      val argListContext = ArgumentListContext(options = options, argNameAsComment = context.argNameAsComment)
      argumentListTraverser.traverse(
        args = init.argss.flatten,
        argTraverser = invocationArgTraverser,
        context = argListContext)
    }
  }
}
