package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter._

import scala.meta.Type

trait TypeListTraverser {
  def traverse(types: List[Type]): Unit
}

private[traversers] class TypeListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                typeTraverser: => TypeTraverser) extends TypeListTraverser {

  override def traverse(types: List[Type]): Unit = {
    if (types.nonEmpty) {
      argumentListTraverser.traverse(args = types,
        // TODO - call the traverser with an argument indicating that Java primitives should be boxed
        argTraverser = typeTraverser,
        maybeEnclosingDelimiter = Some(AngleBracket),
        onSameLine = true)
    }
  }
}
