package effiban.scala2java.traversers

import effiban.scala2java.entities.CtorContext
import effiban.scala2java.transformers.CtorSecondaryTransformer

import scala.meta.Ctor

trait CtorSecondaryTraverser {
  def traverse(secondaryCtor: Ctor.Secondary, ctorContext: CtorContext): Unit
}

private[traversers] class CtorSecondaryTraverserImpl(ctorSecondaryTransformer: CtorSecondaryTransformer,
                                                     defnDefTraverser: => DefnDefTraverser) extends CtorSecondaryTraverser {

  override def traverse(secondaryCtor: Ctor.Secondary, ctorContext: CtorContext): Unit = {
    val defnDef = ctorSecondaryTransformer.transform(secondaryCtor, ctorContext)
    defnDefTraverser.traverse(defnDef, Some(secondaryCtor.init))
  }
}
