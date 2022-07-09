package effiban.scala2java.traversers

import effiban.scala2java.transformers.CtorSecondaryTransformer

import scala.meta.{Ctor, Type}

trait CtorSecondaryTraverser {
  def traverse(secondaryCtor: Ctor.Secondary, className: Type.Name): Unit
}

private[traversers] class CtorSecondaryTraverserImpl(ctorSecondaryTransformer: CtorSecondaryTransformer,
                                                     defnDefTraverser: DefnDefTraverser) extends CtorSecondaryTraverser {

  override def traverse(secondaryCtor: Ctor.Secondary, className: Type.Name): Unit = {
    val defnDef = ctorSecondaryTransformer.transform(secondaryCtor, className)
    defnDefTraverser.traverse(defnDef, Some(secondaryCtor.init))
  }
}

object CtorSecondaryTraverser extends CtorSecondaryTraverserImpl(CtorSecondaryTransformer, DefnDefTraverser)
