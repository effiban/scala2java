package com.effiban.scala2java

import scala.meta.Type

object TypeSingletonTraverser extends ScalaTreeTraverser[Type.Singleton] {

  // A scala expression representing a singleton type like: A.type
  def traverse(singletonType: Type.Singleton): Unit = {
    TermRefTraverser.traverse(singletonType.ref)
    // TODO not sure if something else is needed in Java...
  }
}
