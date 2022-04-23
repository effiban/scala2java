package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Pat

object PatTypedTraverser extends ScalaTreeTraverser[Pat.Typed] {

  // Typed pattern expression, e.g. a: Int (in lhs of case clause)
  override def traverse(typedPattern: Pat.Typed): Unit = {
    GenericTreeTraverser.traverse(typedPattern.rhs)
    emit(" ")
    GenericTreeTraverser.traverse(typedPattern.lhs)
  }
}
