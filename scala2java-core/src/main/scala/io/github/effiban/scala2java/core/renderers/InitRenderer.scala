package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.renderers.contexts.InitRenderContext

import scala.meta.{Init, Term}

trait InitRenderer {
  def render(init: Init, context: InitRenderContext = InitRenderContext()): Unit
}

private[renderers] class InitRendererImpl(typeRenderer: => TypeRenderer,
                                          argumentListRenderer: => ArgumentListRenderer,
                                          invocationArgRenderer: => ArgumentRenderer[Term]) extends InitRenderer {

  // An 'Init' is an instantiated type, such as with `new` or as a parent in a type definition
  override def render(init: Init, context: InitRenderContext = InitRenderContext()): Unit = {
    typeRenderer.render(init.tpe)

    if (!context.ignoreArgs) {
      val options = ListTraversalOptions(traverseEmpty = context.renderEmpty, maybeEnclosingDelimiter = Some(Parentheses))
      val argListContext = ArgumentListContext(options = options, argNameAsComment = context.argNameAsComment)
      argumentListRenderer.render(
        args = init.argss.flatten,
        argRenderer = invocationArgRenderer,
        context = argListContext)
    }
  }
}
