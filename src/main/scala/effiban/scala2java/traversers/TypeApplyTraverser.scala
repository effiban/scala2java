package effiban.scala2java.traversers

import scala.meta.Type

trait TypeApplyTraverser extends ScalaTreeTraverser[Type.Apply]

private[traversers] class TypeApplyTraverserImpl(typeTraverser: => TypeTraverser,
                                                 typeListTraverser: => TypeListTraverser) extends TypeApplyTraverser {

  // type definition with generic args, e.g. F[T]
  override def traverse(typeApply: Type.Apply): Unit = {
    typeTraverser.traverse(typeApply.tpe)
    typeListTraverser.traverse(typeApply.args)
  }
}
