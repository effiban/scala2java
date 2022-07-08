package com.effiban.scala2java

import com.effiban.scala2java.transformers.TypeSingletonTransformer

import scala.meta.Type

trait TypeSingletonTraverser extends ScalaTreeTraverser[Type.Singleton]

private[scala2java] class TypeSingletonTraverserImpl(termTraverser: => TermTraverser,
                                                     typeSingletonTransformer: TypeSingletonTransformer) extends TypeSingletonTraverser {

  override def traverse(singletonType: Type.Singleton): Unit = {
    termTraverser.traverse(typeSingletonTransformer.transform(singletonType))
  }
}

object TypeSingletonTraverser extends TypeSingletonTraverserImpl(TermTraverser, TypeSingletonTransformer)