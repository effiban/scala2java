package com.effiban.scala2java

import scala.meta.Type

trait TypeSingletonTraverser extends ScalaTreeTraverser[Type.Singleton]

private[scala2java] class TypeSingletonTraverserImpl(termRefTraverser: => TermRefTraverser) extends TypeSingletonTraverser {

  // A scala expression representing a singleton type like: A.type
  override def traverse(singletonType: Type.Singleton): Unit = {
    termRefTraverser.traverse(singletonType.ref)
    //TODO not sure if something else is needed in Java...
  }
}

object TypeSingletonTraverser extends TypeSingletonTraverserImpl(TermRefTraverser)