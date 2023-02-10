package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.transformers.FunctionTypeTransformer

import scala.meta.Type

trait TypeFunctionTraverser extends ScalaTreeTraverser[Type.Function]

private[traversers] class TypeFunctionTraverserImpl(typeTraverser: => TypeTraverser,
                                                    functionTypeTransformer: FunctionTypeTransformer)
  extends TypeFunctionTraverser {

  // function type, e.g.: Int => String
  override def traverse(functionType: Type.Function): Unit = {
    typeTraverser.traverse(functionTypeTransformer.transform(functionType))
  }
}
