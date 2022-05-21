package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Type

trait TypeApplyInfixTraverser extends ScalaTreeTraverser[Type.ApplyInfix]

private[scala2java] class TypeApplyInfixTraverserImpl(javaEmitter: JavaEmitter) extends TypeApplyInfixTraverser {

  // type with generic args in infix notation, e.g. K Map V
  override def traverse(typeApplyInfix: Type.ApplyInfix): Unit = {
    //TODO
    emitComment(typeApplyInfix.toString())
  }
}

object TypeApplyInfixTraverser extends TypeApplyInfixTraverserImpl(JavaEmitter)
