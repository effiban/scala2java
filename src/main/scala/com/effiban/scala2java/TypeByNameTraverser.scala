package com.effiban.scala2java

import scala.meta.Type

trait TypeByNameTraverser extends ScalaTreeTraverser[Type.ByName]

private[scala2java] class TypeByNameTraverserImpl(typeApplyTraverser: => TypeApplyTraverser) extends TypeByNameTraverser {

  // Type by name, e.g.: =>T in f(x: => T)
  override def traverse(typeByName: Type.ByName): Unit = {
    // The closest analogue in Java is Supplier
    typeApplyTraverser.traverse(Type.Apply(Type.Name("Supplier"), List(typeByName.tpe)))
  }
}

object TypeByNameTraverser extends TypeByNameTraverserImpl(TypeApplyTraverser)