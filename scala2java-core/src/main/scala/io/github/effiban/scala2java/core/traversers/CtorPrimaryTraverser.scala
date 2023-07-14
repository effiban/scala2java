package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{CtorContext, DefnDefContext, DefnDefRenderContext}
import io.github.effiban.scala2java.core.renderers.DefnDefRenderer
import io.github.effiban.scala2java.core.transformers.CtorPrimaryTransformer

import scala.meta.Ctor

trait CtorPrimaryTraverser {
  def traverse(primaryCtor: Ctor.Primary, ctorContext: CtorContext): Unit
}

private[traversers] class CtorPrimaryTraverserImpl(ctorPrimaryTransformer: CtorPrimaryTransformer,
                                                   defnDefTraverser: => DefnDefTraverser,
                                                   defnDefRenderer: => DefnDefRenderer) extends CtorPrimaryTraverser {

  override def traverse(primaryCtor: Ctor.Primary, context: CtorContext): Unit = {
    val defnDef = ctorPrimaryTransformer.transform(primaryCtor, context)
    val traversalResult = defnDefTraverser.traverse(defnDef, DefnDefContext(context.javaScope))
    val renderContext = DefnDefRenderContext(traversalResult.javaModifiers)
    defnDefRenderer.render(traversalResult.tree, renderContext)
  }
}
