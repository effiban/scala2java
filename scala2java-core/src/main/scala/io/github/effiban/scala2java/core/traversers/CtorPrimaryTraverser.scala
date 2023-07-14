package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{CtorContext, DefnDefContext}
import io.github.effiban.scala2java.core.transformers.CtorPrimaryTransformer
import io.github.effiban.scala2java.core.traversers.results.DefnDefTraversalResult

import scala.meta.Ctor

trait CtorPrimaryTraverser {
  def traverse(primaryCtor: Ctor.Primary, ctorContext: CtorContext): DefnDefTraversalResult
}

private[traversers] class CtorPrimaryTraverserImpl(ctorPrimaryTransformer: CtorPrimaryTransformer,
                                                   defnDefTraverser: => DefnDefTraverser) extends CtorPrimaryTraverser {

  override def traverse(primaryCtor: Ctor.Primary, context: CtorContext): DefnDefTraversalResult = {
    val defnDef = ctorPrimaryTransformer.transform(primaryCtor, context)
    defnDefTraverser.traverse(defnDef, DefnDefContext(context.javaScope))
  }
}
