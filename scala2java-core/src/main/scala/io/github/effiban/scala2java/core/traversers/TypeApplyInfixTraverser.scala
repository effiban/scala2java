package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

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
