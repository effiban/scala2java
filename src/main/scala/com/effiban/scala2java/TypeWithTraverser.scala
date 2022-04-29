package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Type

trait TypeWithTraverser extends ScalaTreeTraverser[Type.With]

object TypeWithTraverser extends TypeWithTraverser {

  // type with parent, e.g.  A with B
  // approximated by Java "extends" but might not compile
  override def traverse(typeWith: Type.With): Unit = {
    TypeTraverser.traverse(typeWith.lhs)
    emit(" extends ")
    TypeTraverser.traverse(typeWith.rhs)
  }
}
