package io.github.effiban.scala2java.core.traversers

import scala.meta.Type

trait TypeSelectTraverser extends ScalaTreeTraverser1[Type.Select]

private[traversers] class TypeSelectTraverserImpl(defaultTermRefTraverser: => DefaultTermRefTraverser,
                                                  typeNameTraverser: TypeNameTraverser) extends TypeSelectTraverser {

  // A qualified type (e.g.: a.b.C where 'a.b' is a package and C is a class inside it)
  override def traverse(typeSelect: Type.Select): Type.Select = {
    val traversedQual = defaultTermRefTraverser.traverse(typeSelect.qual)
    val traversedTypeName = typeNameTraverser.traverse(typeSelect.name)
    Type.Select(traversedQual, traversedTypeName)
  }
}
