package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.NameRenderer

import scala.meta.Type

trait TypeParamTraverser extends ScalaTreeTraverser[Type.Param]

private[traversers] class TypeParamTraverserImpl(nameTraverser: NameTraverser,
                                                 nameRenderer: NameRenderer,
                                                 typeParamListTraverser: => TypeParamListTraverser,
                                                 typeBoundsTraverser: => TypeBoundsTraverser) extends TypeParamTraverser {

  // Type param declaration, e.g.: `T` in trait MyTrait[T]
  override def traverse(typeParam: Type.Param): Unit = {
    //TODO handle mods (such as variance mods)
    val traversedName = nameTraverser.traverse(typeParam.name)
    nameRenderer.render(traversedName)
    typeParamListTraverser.traverse(typeParam.tparams)
    typeBoundsTraverser.traverse(typeParam.tbounds)
    //TODO handle vbounds and cbounds (which aren't supported in Java, maybe partially ?)
  }
}
