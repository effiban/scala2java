package effiban.scala2java.traversers

import effiban.scala2java.AngleBracket

import scala.meta.Type

trait TypeParamListTraverser {
  def traverse(typeParams: List[Type.Param]): Unit
}

private[scala2java] class TypeParamListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                     typeParamTraverser: => TypeParamTraverser) extends TypeParamListTraverser {

  override def traverse(typeParams: List[Type.Param]): Unit = {
    if (typeParams.nonEmpty) {
      argumentListTraverser.traverse(args = typeParams,
        argTraverser = typeParamTraverser,
        maybeDelimiterType = Some(AngleBracket),
        onSameLine = true)
    }
  }
}

object TypeParamListTraverser extends TypeParamListTraverserImpl(ArgumentListTraverser, TypeParamTraverser)