package io.github.effiban.scala2java.core.traversers

import scala.meta.Type

trait TypeParamTraverser extends ScalaTreeTraverser1[Type.Param]

private[traversers] class TypeParamTraverserImpl(nameTraverser: NameTraverser,
                                                 typeBoundsTraverser: => TypeBoundsTraverser) extends TypeParamTraverser {

  // Type param declaration, e.g.: `T` in trait MyTrait[T]
  override def traverse(typeParam: Type.Param): Type.Param = {
    import typeParam._
    //TODO handle mods (such as variance mods)
    val traversedName = nameTraverser.traverse(name)
    val traversedTypeParams = tparams.map(traverse)
    val traversedTypeBounds = typeBoundsTraverser.traverse(tbounds)
    //TODO handle vbounds and cbounds (which aren't supported in Java, maybe partially ?)
    typeParam.copy(
      name = traversedName,
      tparams = traversedTypeParams,
      tbounds = traversedTypeBounds
    )
  }
}
