package effiban.scala2java.traversers

import effiban.scala2java.contexts.{CtorContext, DefnDefContext}
import effiban.scala2java.transformers.CtorPrimaryTransformer

import scala.meta.Ctor

trait CtorPrimaryTraverser {
  def traverse(primaryCtor: Ctor.Primary, ctorContext: CtorContext): Unit
}

private[traversers] class CtorPrimaryTraverserImpl(ctorPrimaryTransformer: CtorPrimaryTransformer,
                                                   defnDefTraverser: => DefnDefTraverser) extends CtorPrimaryTraverser {

  override def traverse(primaryCtor: Ctor.Primary, context: CtorContext): Unit = {
    val defnDef = ctorPrimaryTransformer.transform(primaryCtor, context)
    defnDefTraverser.traverse(defnDef, DefnDefContext(context.javaScope))
  }
}
