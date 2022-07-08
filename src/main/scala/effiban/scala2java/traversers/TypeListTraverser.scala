package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter._

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
        maybeEnclosingDelimiter = Some(AngleBracket),
        onSameLine = true)
    }
  }
}

object TypeListTraverser extends TypeListTraverserImpl(ArgumentListTraverser, TypeTraverser)
