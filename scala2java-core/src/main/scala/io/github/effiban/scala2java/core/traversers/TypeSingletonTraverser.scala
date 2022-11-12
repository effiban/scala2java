package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.transformers.TypeSingletonToTermTransformer

import scala.meta.Type

trait TypeSingletonTraverser extends ScalaTreeTraverser[Type.Singleton]

private[traversers] class TypeSingletonTraverserImpl(termTraverser: => TermTraverser,
                                                     typeSingletonToTermTransformer: TypeSingletonToTermTransformer)
  extends TypeSingletonTraverser {

  override def traverse(singletonType: Type.Singleton): Unit = {
    termTraverser.traverse(typeSingletonToTermTransformer.transform(singletonType))
  }
}