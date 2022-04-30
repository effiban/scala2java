package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Type

trait TypeSelectTraverser extends ScalaTreeTraverser[Type.Select]

private[scala2java] class TypeSelectTraverserImpl(termRefTraverser: => TermRefTraverser,
                                                  typeNameTraverser: => TypeNameTraverser) extends TypeSelectTraverser {

  // A scala type selecting expression like: a.B
  override def traverse(typeSelect: Type.Select): Unit = {
    termRefTraverser.traverse(typeSelect.qual)
    emit(".")
    typeNameTraverser.traverse(typeSelect.name)
  }
}

object TypeSelectTraverser extends TypeSelectTraverserImpl(TermRefTraverser, TypeNameTraverser)