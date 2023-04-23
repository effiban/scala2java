package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.spi.transformers.TypeNameTransformer

import scala.meta.Type

trait TypeNameTraverser extends ScalaTreeTraverser1[Type.Name]

private[traversers] class TypeNameTraverserImpl(typeNameTransformer: TypeNameTransformer) extends TypeNameTraverser {

  override def traverse(name: Type.Name): Type.Name = typeNameTransformer.transform(name)
}
