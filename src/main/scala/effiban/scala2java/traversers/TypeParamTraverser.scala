package effiban.scala2java.traversers

import scala.meta.Type

trait TypeParamTraverser extends ScalaTreeTraverser[Type.Param]

private[traversers] class TypeParamTraverserImpl(nameTraverser: => NameTraverser,
                                                 typeParamListTraverser: => TypeParamListTraverser,
                                                 typeBoundsTraverser: => TypeBoundsTraverser) extends TypeParamTraverser {

  // Type param declaration, e.g.: `T` in trait MyTrait[T]
  override def traverse(typeParam: Type.Param): Unit = {
    //TODO handle mods (such as variance mods)
    nameTraverser.traverse(typeParam.name)
    typeParamListTraverser.traverse(typeParam.tparams)
    typeBoundsTraverser.traverse(typeParam.tbounds)
    //TODO handle vbounds and cbounds (which aren't supported in Java, maybe partially ?)
  }
}
