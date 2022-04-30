package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Type

trait TypeWithTraverser extends ScalaTreeTraverser[Type.With]

private[scala2java] class TypeWithTraverserImpl(typeTraverser: => TypeTraverser) extends TypeWithTraverser {

  // type with parent, e.g.  A with B
  // approximated by Java "extends" but might not compile
  override def traverse(typeWith: Type.With): Unit = {
    typeTraverser.traverse(typeWith.lhs)
    emit(" extends ")
    typeTraverser.traverse(typeWith.rhs)
  }
}

object TypeWithTraverser extends TypeWithTraverserImpl(TypeTraverser)