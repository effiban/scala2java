package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter
import effiban.scala2java.writers.JavaWriter.writeComment

import scala.meta.Type

trait TypeApplyInfixTraverser extends ScalaTreeTraverser[Type.ApplyInfix]

private[scala2java] class TypeApplyInfixTraverserImpl(javaWriter: JavaWriter) extends TypeApplyInfixTraverser {

  // type with generic args in infix notation, e.g. K Map V
  override def traverse(typeApplyInfix: Type.ApplyInfix): Unit = {
    //TODO
    writeComment(typeApplyInfix.toString())
  }
}

object TypeApplyInfixTraverser extends TypeApplyInfixTraverserImpl(JavaWriter)
