package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeLambdaRenderer

import scala.meta.Type

trait TypeLambdaTraverser extends ScalaTreeTraverser[Type.Lambda]

private[traversers] class TypeLambdaTraverserImpl(typeLambdaRenderer: TypeLambdaRenderer) extends TypeLambdaTraverser {

  // higher-kinded type, e.g. [K, V] =>> Map[K, V]
  // According to documentation supported only in some dialects (what does this mean?)
  override def traverse(lambdaType: Type.Lambda): Unit = {
    typeLambdaRenderer.render(lambdaType)
  }
}
