package com.effiban.scala2java

import scala.meta.Type

object TypeByNameTraverser extends ScalaTreeTraverser[Type.ByName] {

  // Type by name, e.g.: =>T in f(x: => T)
  def traverse(typeByName: Type.ByName): Unit = {
    // The closest analogue in Java is Supplier
    TypeApplyTraverser.traverse(Type.Apply(Type.Name("Supplier"), List(typeByName.tpe)))
  }
}
