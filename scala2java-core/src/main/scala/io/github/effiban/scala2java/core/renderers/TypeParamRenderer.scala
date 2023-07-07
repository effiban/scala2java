package io.github.effiban.scala2java.core.renderers

import scala.meta.Type

trait TypeParamRenderer extends JavaTreeRenderer[Type.Param]

private[renderers] class TypeParamRendererImpl(nameRenderer: NameRenderer,
                                               typeParamListRenderer: => TypeParamListRenderer,
                                               typeBoundsRenderer: => TypeBoundsRenderer) extends TypeParamRenderer {

  // Type param declaration, e.g.: `T` in trait MyTrait[T]
  override def render(typeParam: Type.Param): Unit = {
    //TODO handle mods (such as variance mods)
    nameRenderer.render(typeParam.name)
    typeParamListRenderer.render(typeParam.tparams)
    typeBoundsRenderer.render(typeParam.tbounds)
    //TODO handle vbounds and cbounds (which aren't supported in Java, maybe partially ?)
  }
}
