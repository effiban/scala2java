package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.NameRenderer

import scala.meta.{Name, Type}

trait NameTraverser extends ScalaTreeTraverser[Name]

private[traversers] class NameTraverserImpl(typeNameTraverser: TypeNameTraverser,
                                            nameRenderer: NameRenderer) extends NameTraverser {

  override def traverse(name: Name): Unit = {
    val traversedName = name match {
      case typeName: Type.Name => typeNameTraverser.traverse(typeName)
      case other => other
    }
    nameRenderer.render(traversedName)
  }
}
