package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Type

trait TypeLambdaTraverser extends ScalaTreeTraverser[Type.Lambda]

object TypeLambdaTraverser extends TypeLambdaTraverser {

  // generic lambda type [T] => (T, T)
  // According to documentation supported only in some dialects (what does this mean?)
  override def traverse(lambdaType: Type.Lambda): Unit = {
    // TODO maybe convert simple case to Java
    emitComment(lambdaType.toString())
  }
}
