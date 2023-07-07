package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.{NameRenderer, TypeBoundsRenderer}

import scala.meta.Type

@deprecated
trait DeprecatedTypeParamTraverser extends ScalaTreeTraverser[Type.Param]

@deprecated
private[traversers] class DeprecatedTypeParamTraverserImpl(nameTraverser: NameTraverser,
                                                           nameRenderer: NameRenderer,
                                                           typeParamListTraverser: => DeprecatedTypeParamListTraverser,
                                                           typeBoundsTraverser: => TypeBoundsTraverser,
                                                           typeBoundsRenderer: => TypeBoundsRenderer) extends DeprecatedTypeParamTraverser {

  // Type param declaration, e.g.: `T` in trait MyTrait[T]
  override def traverse(typeParam: Type.Param): Unit = {
    //TODO handle mods (such as variance mods)
    val traversedName = nameTraverser.traverse(typeParam.name)
    nameRenderer.render(traversedName)
    typeParamListTraverser.traverse(typeParam.tparams)
    val traversedTypeBounds = typeBoundsTraverser.traverse(typeParam.tbounds)
    typeBoundsRenderer.render(traversedTypeBounds)
    //TODO handle vbounds and cbounds (which aren't supported in Java, maybe partially ?)
  }
}
