package com.effiban.scala2java

import scala.meta.Type

object TypeParamTraverser extends ScalaTreeTraverser[Type.Param] {

  // Type param declaration, e.g.: `T` in trait MyTrait[T]
  def traverse(typeParam: Type.Param): Unit = {
    // TODO handle mods
    GenericTreeTraverser.traverse(typeParam.name)
    TypeListTraverser.traverse(typeParam.tparams)
    GenericTreeTraverser.traverse(typeParam.tbounds)
    // TODO handle vbounds and cbounds (which aren't supported in Java, maybe partially ?)
  }
}
