package effiban.scala2java.traversers

import effiban.scala2java.entities.CtorContext
import effiban.scala2java.transformers.CtorPrimaryTransformer

import scala.meta.Ctor

trait CtorPrimaryTraverser {
  def traverse(primaryCtor: Ctor.Primary, ctorContext: CtorContext): Unit
}

private[traversers] class CtorPrimaryTraverserImpl(ctorPrimaryTransformer: CtorPrimaryTransformer,
                                                   defnDefTraverser: => DefnDefTraverser) extends CtorPrimaryTraverser {

  override def traverse(primaryCtor: Ctor.Primary, ctorContext: CtorContext): Unit = {
    val defnDef = ctorPrimaryTransformer.transform(primaryCtor, ctorContext)
    defnDefTraverser.traverse(defnDef)
  }
}
