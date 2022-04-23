package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Type

object TypeApplyInfixTraverser extends ScalaTreeTraverser[Type.ApplyInfix] {

  // type with generic args in infix notation, e.g. K Map V
  override def traverse(typeApplyInfix: Type.ApplyInfix): Unit = {
    // TODO
    emitComment(typeApplyInfix.toString())
  }
}
