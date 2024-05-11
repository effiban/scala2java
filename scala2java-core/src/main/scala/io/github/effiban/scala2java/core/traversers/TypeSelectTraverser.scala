package io.github.effiban.scala2java.core.traversers

import scala.meta.Type

trait TypeSelectTraverser extends ScalaTreeTraverser2[Type.Select, Type]

private[traversers] class TypeSelectTraverserImpl(defaultTermRefTraverser: => DefaultTermRefTraverser) extends TypeSelectTraverser {

  // A qualified type (e.g.: a.b.C where 'a.b' is a package and C is a class inside it)
  override def traverse(typeSelect: Type.Select): Type = {
    val traversedQual = defaultTermRefTraverser.traverse(typeSelect.qual)
    Type.Select(traversedQual, typeSelect.name)
  }
}
