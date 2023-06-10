package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerRenderContextResolver

import scala.meta.Term

trait TermApplyRenderer extends JavaTreeRenderer[Term.Apply]

private[renderers] class TermApplyRendererImpl(expressionTermRenderer: => ExpressionTermRenderer,
                                               arrayInitializerRenderer: => ArrayInitializerRenderer,
                                               argumentListRenderer: => ArgumentListRenderer,
                                               invocationArgRenderer: => InvocationArgRenderer[Term],
                                               arrayInitializerRenderContextResolver: ArrayInitializerRenderContextResolver)
  extends TermApplyRenderer {

  override def render(termApply: Term.Apply): Unit = {
    arrayInitializerRenderContextResolver.tryResolve(termApply) match {
      case Some(context) => arrayInitializerRenderer.renderWithValues(context)
      case None => renderRegular(termApply)
    }
  }

  private def renderRegular(termApply: Term.Apply): Unit = {
    expressionTermRenderer.render(termApply.fun)
    val options = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses), traverseEmpty = true)
    val argListContext = ArgumentListContext(options = options, argNameAsComment = true)
    argumentListRenderer.render(
      args = termApply.args,
      argRendererProvider = _ => invocationArgRenderer,
      context = argListContext
    )
  }
}
