package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypeApplyInfixTraverser extends ScalaTreeTraverser[Type.ApplyInfix]

private[traversers] class TypeApplyInfixTraverserImpl(implicit javaWriter: JavaWriter) extends TypeApplyInfixTraverser {

  import javaWriter._

  // type with generic args in infix notation, e.g. K Map V
  override def traverse(typeApplyInfix: Type.ApplyInfix): Unit = {
    //TODO
    writeComment(typeApplyInfix.toString())
  }
}
