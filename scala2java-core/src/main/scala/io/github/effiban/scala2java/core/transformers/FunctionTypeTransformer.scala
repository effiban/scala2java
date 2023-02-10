package io.github.effiban.scala2java.core.transformers

import scala.meta.{Type, XtensionQuasiquoteType}

trait FunctionTypeTransformer {
  def transform(functionType: Type.Function): Type
}

object FunctionTypeTransformer extends FunctionTypeTransformer {

  // Transform a Scala function type (e.g.: Int => String) into a Java function type (e.g. Function<Int, String>)
  override def transform(functionType: Type.Function): Type = {
    (functionType.params, functionType.res) match {
      case (Nil | List(t"Unit") , t"Unit") => t"Runnable"
      case (Nil | List(t"Unit"), res) => Type.Apply(Type.Name("Supplier"), List(res))
      case (List(param), t"Unit") => Type.Apply(Type.Name("Consumer"), List(param))
      case (List(param), res) => Type.Apply(Type.Name("Function"), List(param, res))
      case (List(param1, param2), t"Unit") => Type.Apply(Type.Name("BiConsumer"), List(param1, param2))
      case (List(param1, param2), res) => Type.Apply(Type.Name("BiFunction"), List(param1, param2, res))
      // Since Java doesn't have built-in support for a function type of more than 2 params, using JOOL instead
      case (params, res) => Type.Apply(Type.Name(joolFunctionTypeOf(params)), params :+ res)
    }
  }

  private def joolFunctionTypeOf(params: List[Type]) = s"Function${params.length}"
}
