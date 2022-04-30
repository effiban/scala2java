package com.effiban.scala2java

import scala.meta.Type

trait TypeListTraverser {
  def traverse(types: List[Type]): Unit
}

private[scala2java] class TypeListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                typeTraverser: => TypeTraverser) extends TypeListTraverser {

  override def traverse(types: List[Type]): Unit = {
    if (types.nonEmpty) {
      argumentListTraverser.traverse(args = types,
        argTraverser = typeTraverser,
        maybeDelimiterType = Some(AngleBracket),
        onSameLine = true)
    }
  }
}

object TypeListTraverser extends TypeListTraverserImpl(ArgumentListTraverser, TypeTraverser)
