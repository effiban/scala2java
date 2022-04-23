package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Type

object TypeWithTraverser extends ScalaTreeTraverser[Type.With] {

  // type with parent, e.g.  A with B
  // approximated by Java "extends" but might not compile
  def traverse(typeWith: Type.With): Unit = {
    GenericTreeTraverser.traverse(typeWith.lhs)
    emit(" extends ")
    GenericTreeTraverser.traverse(typeWith.rhs)
  }
}
