package effiban.scala2java.traversers

import effiban.scala2java.transformers.CtorPrimaryTransformer

import scala.meta.{Ctor, Init, Type}

trait CtorPrimaryTraverser {
  def traverse(primaryCtor: Ctor.Primary, className: Type.Name, inits: List[Init]): Unit
}

private[traversers] class CtorPrimaryTraverserImpl(ctorPrimaryTransformer: CtorPrimaryTransformer,
                                                   defnDefTraverser: => DefnDefTraverser) extends CtorPrimaryTraverser {

  override def traverse(primaryCtor: Ctor.Primary, className: Type.Name, inits: List[Init]): Unit = {
    val defnDef = ctorPrimaryTransformer.transform(primaryCtor, className, inits)
    defnDefTraverser.traverse(defnDef)
  }
}
