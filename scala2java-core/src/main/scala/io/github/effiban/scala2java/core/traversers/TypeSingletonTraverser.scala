package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.transformers.TypeSingletonToTermTransformer

import scala.meta.Type

trait TypeSingletonTraverser extends ScalaTreeTraverser[Type.Singleton]

private[traversers] class TypeSingletonTraverserImpl(defaultTermTraverser: => DefaultTermTraverser,
                                                     typeSingletonToTermTransformer: TypeSingletonToTermTransformer)
  extends TypeSingletonTraverser {

  override def traverse(singletonType: Type.Singleton): Unit = {
    defaultTermTraverser.traverse(typeSingletonToTermTransformer.transform(singletonType))
  }
}
