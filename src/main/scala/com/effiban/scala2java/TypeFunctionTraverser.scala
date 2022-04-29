package com.effiban.scala2java

import scala.meta.Type

trait TypeFunctionTraverser extends ScalaTreeTraverser[Type.Function]

object TypeFunctionTraverser extends TypeFunctionTraverser {

  // function type, e.g.: Int => String
  override def traverse(functionType: Type.Function): Unit = {
    // TODO - handle more than one input param (in Java we can use BiFunction for 2, but more than that will have to be a comment)
    TypeApplyTraverser.traverse(Type.Apply(Type.Name("Function"), functionType.params :+ functionType.res))
  }
}
