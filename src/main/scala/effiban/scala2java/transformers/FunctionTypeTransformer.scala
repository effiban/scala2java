package effiban.scala2java.transformers

import scala.meta.Type

trait FunctionTypeTransformer {
  def transform(functionType: Type.Function): Type.Apply
}

object FunctionTypeTransformer extends FunctionTypeTransformer {

  // Transform a Scala function type (e.g.: Int => String) into a Java function type (e.g. Function<Int, String>)
  override def transform(functionType: Type.Function): Type.Apply = {
    functionType.params match {
      case Nil => Type.Apply(Type.Name("Supplier"), List(functionType.res))
      case param :: Nil => Type.Apply(Type.Name("Function"), List(param, functionType.res))
      case param1 :: param2 :: Nil => Type.Apply(Type.Name("BiFunction"), List(param1, param2, functionType.res))
      // Since Java doesn't have built-in support for a function type of more than 2 params, using JOOL instead
      case params => Type.Apply(Type.Name(joolFunctionTypeOf(params)), params :+ functionType.res)
    }
  }

  private def joolFunctionTypeOf(params: List[Type]) = s"Function${params.length}"
}
