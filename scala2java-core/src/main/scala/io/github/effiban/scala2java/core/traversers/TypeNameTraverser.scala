package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeNameRenderer
import io.github.effiban.scala2java.spi.transformers.TypeNameTransformer

import scala.meta.Type

trait TypeNameTraverser extends ScalaTreeTraverser[Type.Name]

private[traversers] class TypeNameTraverserImpl(typeNameTransformer: TypeNameTransformer,
                                                typeNameRenderer: TypeNameRenderer) extends TypeNameTraverser {

  override def traverse(name: Type.Name): Unit = {
    val transformedTypeName = typeNameTransformer.transform(name)
    typeNameRenderer.render(transformedTypeName)
  }
}
