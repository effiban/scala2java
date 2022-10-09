package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{InitContext, InvocationArgListContext}

import scala.meta.Init

trait InitTraverser {
  def traverse(init: Init, context: InitContext = InitContext()): Unit
}

private[traversers] class InitTraverserImpl(typeTraverser: => TypeTraverser,
                                            invocationArgListTraverser: => InvocationArgListTraverser) extends InitTraverser {

  // An 'Init' is an instantiated type, such as with `new` or as a parent in a type definition
  override def traverse(init: Init, context: InitContext = InitContext()): Unit = {
    typeTraverser.traverse(init.tpe)

    if (!context.ignoreArgs) {
      val invocationArgListContext = InvocationArgListContext(
        traverseEmpty = context.traverseEmpty,
        argNameAsComment = context.argNameAsComment
      )
      invocationArgListTraverser.traverse(init.argss.flatten, invocationArgListContext)
    }
  }
}
