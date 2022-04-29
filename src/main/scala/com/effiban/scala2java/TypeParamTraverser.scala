package com.effiban.scala2java

import scala.meta.Type

trait TypeParamTraverser extends ScalaTreeTraverser[Type.Param]

object TypeParamTraverser extends TypeParamTraverser {

  // Type param declaration, e.g.: `T` in trait MyTrait[T]
  override def traverse(typeParam: Type.Param): Unit = {
    // TODO handle mods
    NameTraverser.traverse(typeParam.name)
    TypeParamListTraverser.traverse(typeParam.tparams)
    TypeBoundsTraverser.traverse(typeParam.tbounds)
    // TODO handle vbounds and cbounds (which aren't supported in Java, maybe partially ?)
  }
}
