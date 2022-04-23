package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Type

object TypeSingletonTraverser extends ScalaTreeTraverser[Type.Singleton] {

  // A scala expression representing the type of a singleton like: A.type
  def traverse(singletonType: Type.Singleton): Unit = {
    TermRefTraverser.traverse(singletonType.ref)
    // Not sure about this, might only work sometimes
    emit(".class")
  }
}
