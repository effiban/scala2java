package io.github.effiban.scala2java.core.traversers

import scala.meta.Type

trait TypeFunctionTraverser extends ScalaTreeTraverser1[Type.Function]

private[traversers] class TypeFunctionTraverserImpl(typeTraverser: => TypeTraverser)
  extends TypeFunctionTraverser {

  // function type, e.g.: Int => String
  override def traverse(functionType: Type.Function): Type.Function = {
    Type.Function(
      params = functionType.params.map(typeTraverser.traverse),
      res = typeTraverser.traverse(functionType.res)
    )
  }
}
