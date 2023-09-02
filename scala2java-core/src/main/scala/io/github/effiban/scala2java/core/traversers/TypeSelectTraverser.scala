package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.spi.transformers.TypeSelectTransformer

import scala.meta.Type

trait TypeSelectTraverser extends ScalaTreeTraverser2[Type.Select, Type]

private[traversers] class TypeSelectTraverserImpl(defaultTermRefTraverser: => DefaultTermRefTraverser,
                                                  typeNameTraverser: TypeNameTraverser,
                                                  typeSelectTransformer: TypeSelectTransformer) extends TypeSelectTraverser {

  // A qualified type (e.g.: a.b.C where 'a.b' is a package and C is a class inside it)
  override def traverse(typeSelect: Type.Select): Type = {
    typeSelectTransformer.transform(typeSelect) match {
      case Some(transformedType) => transformedType
      case None =>
        val traversedQual = defaultTermRefTraverser.traverse(typeSelect.qual)
        val traversedTypeName = typeNameTraverser.traverse(typeSelect.name)
        Type.Select(traversedQual, traversedTypeName)
    }
  }
}
