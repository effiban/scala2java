package effiban.scala2java

import scala.meta.Type

trait TypeApplyTraverser extends ScalaTreeTraverser[Type.Apply]

private[scala2java] class TypeApplyTraverserImpl(typeTraverser: => TypeTraverser,
                                                 typeListTraverser: => TypeListTraverser) extends TypeApplyTraverser {

  // type definition with generic args, e.g. F[T]
  override def traverse(typeApply: Type.Apply): Unit = {
    typeTraverser.traverse(typeApply.tpe)
    typeListTraverser.traverse(typeApply.args)
  }
}

object TypeApplyTraverser extends TypeApplyTraverserImpl(TypeTraverser, TypeListTraverser)