package effiban.scala2java.traversers

import effiban.scala2java.contexts.{CtorContext, DefnDefContext}
import effiban.scala2java.entities.JavaTreeType.Unknown
import effiban.scala2java.transformers.CtorSecondaryTransformer

import scala.meta.Ctor

trait CtorSecondaryTraverser {
  def traverse(secondaryCtor: Ctor.Secondary, ctorContext: CtorContext): Unit
}

private[traversers] class CtorSecondaryTraverserImpl(ctorSecondaryTransformer: CtorSecondaryTransformer,
                                                     defnDefTraverser: => DefnDefTraverser) extends CtorSecondaryTraverser {

  override def traverse(secondaryCtor: Ctor.Secondary, ctorContext: CtorContext): Unit = {
    val defnDef = ctorSecondaryTransformer.transform(secondaryCtor, ctorContext)
    val defnDefContext = DefnDefContext(javaScope = Unknown, maybeInit = Some(secondaryCtor.init))
    defnDefTraverser.traverse(defnDef, defnDefContext)
  }
}
