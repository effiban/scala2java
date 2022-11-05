package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{CtorContext, DefnDefContext}
import io.github.effiban.scala2java.core.transformers.CtorSecondaryTransformer

import scala.meta.Ctor

trait CtorSecondaryTraverser {
  def traverse(secondaryCtor: Ctor.Secondary, ctorContext: CtorContext): Unit
}

private[traversers] class CtorSecondaryTraverserImpl(ctorSecondaryTransformer: CtorSecondaryTransformer,
                                                     defnDefTraverser: => DefnDefTraverser) extends CtorSecondaryTraverser {

  override def traverse(secondaryCtor: Ctor.Secondary, context: CtorContext): Unit = {
    val defnDef = ctorSecondaryTransformer.transform(secondaryCtor, context)
    val defnDefContext = DefnDefContext(javaScope = context.javaScope, maybeInit = Some(secondaryCtor.init))
    defnDefTraverser.traverse(defnDef, defnDefContext)
  }
}
