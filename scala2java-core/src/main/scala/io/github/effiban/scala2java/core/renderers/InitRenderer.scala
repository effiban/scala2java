package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ArgumentListContext, InitContext}
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions

import scala.meta.{Init, Term}

trait InitRenderer {
  def render(init: Init, context: InitContext = InitContext()): Unit
}

private[renderers] class InitRendererImpl(typeRenderer: => TypeRenderer,
                                          argumentListRenderer: => ArgumentListRenderer,
                                          invocationArgRenderer: => ArgumentRenderer[Term]) extends InitRenderer {

  // An 'Init' is an instantiated type, such as with `new` or as a parent in a type definition
  override def render(init: Init, context: InitContext = InitContext()): Unit = {
    typeRenderer.render(init.tpe)

    if (!context.ignoreArgs) {
      val options = ListTraversalOptions(traverseEmpty = context.traverseEmpty, maybeEnclosingDelimiter = Some(Parentheses))
      val argListContext = ArgumentListContext(options = options, argNameAsComment = context.argNameAsComment)
      argumentListRenderer.render(
        args = init.argss.flatten,
        argRendererProvider = _ => invocationArgRenderer,
        context = argListContext)
    }
  }
}
