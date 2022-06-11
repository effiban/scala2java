package com.effiban.scala2java

import com.effiban.scala2java.transformers.CtorPrimaryTransformer

import scala.meta.{Ctor, Init, Type}

trait CtorPrimaryTraverser {
  def traverse(primaryCtor: Ctor.Primary, className: Type.Name, inits: List[Init]): Unit
}

private[scala2java] class CtorPrimaryStatTraverserImpl(ctorPrimaryStatTransformer: CtorPrimaryTransformer,
                                                       defnDefTraverser: DefnDefTraverser) extends CtorPrimaryTraverser {

  override def traverse(primaryCtor: Ctor.Primary, className: Type.Name, inits: List[Init]): Unit = {
    val defnDef = ctorPrimaryStatTransformer.transform(primaryCtor, className, inits)
    defnDefTraverser.traverse(defnDef)
  }
}

object CtorPrimaryTraverser extends CtorPrimaryStatTraverserImpl(CtorPrimaryTransformer, DefnDefTraverser)
