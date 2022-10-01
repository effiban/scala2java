package effiban.scala2java.traversers

import effiban.scala2java.transformers.FunctionTypeTransformer
import effiban.scala2java.writers.JavaWriter

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
