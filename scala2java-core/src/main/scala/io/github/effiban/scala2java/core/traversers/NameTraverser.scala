package io.github.effiban.scala2java.core.traversers

import scala.meta.{Name, Type}

trait NameTraverser extends ScalaTreeTraverser1[Name]

private[traversers] class NameTraverserImpl(typeNameTraverser: TypeNameTraverser) extends NameTraverser {

  override def traverse(name: Name): Name = name match {
    case typeName: Type.Name => typeNameTraverser.traverse(typeName)
    case other => other
  }
}
