package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.transformers.FunctionTypeTransformer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeFunctionTraverser extends ScalaTreeTraverser[Type.Function]

private[traversers] class TypeFunctionTraverserImpl(typeApplyTraverser: => TypeApplyTraverser,
                                                    functionTypeTransformer: FunctionTypeTransformer)
                                                   (implicit javaWriter: JavaWriter)
  extends TypeFunctionTraverser {

  // function type, e.g.: Int => String
  override def traverse(functionType: Type.Function): Unit = {
    typeApplyTraverser.traverse(functionTypeTransformer.transform(functionType))
  }
}
