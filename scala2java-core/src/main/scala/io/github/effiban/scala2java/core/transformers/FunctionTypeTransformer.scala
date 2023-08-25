package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TypeNameValues
import io.github.effiban.scala2java.core.entities.TypeNameValues.{JavaBiConsumer, JavaBiFunction, JavaConsumer, JavaRunnable, JavaSupplier}

import scala.meta.{Type, XtensionQuasiquoteType}

trait FunctionTypeTransformer {
  def transform(functionType: Type.Function): Type
}

object FunctionTypeTransformer extends FunctionTypeTransformer {

  // Transform a Scala function type (e.g.: Int => String) into a Java function type (e.g. Function<Int, String>)
  override def transform(functionType: Type.Function): Type = {
    (functionType.params, functionType.res) match {
      case (Nil | List(t"scala.Unit") , t"scala.Unit") => Type.Name(JavaRunnable)
      case (Nil | List(t"scala.Unit"), res) => Type.Apply(Type.Name(JavaSupplier), List(res))
      case (List(param), t"scala.Unit") => Type.Apply(Type.Name(JavaConsumer), List(param))
      case (List(param), res) => Type.Apply(Type.Name(TypeNameValues.Function), List(param, res))
      case (List(param1, param2), t"scala.Unit") => Type.Apply(Type.Name(JavaBiConsumer), List(param1, param2))
      case (List(param1, param2), res) => Type.Apply(Type.Name(JavaBiFunction), List(param1, param2, res))
      // Since Java doesn't have built-in support for a function type of more than 2 params, using JOOL instead
      case (params, res) => Type.Apply(Type.Name(joolFunctionTypeOf(params)), params :+ res)
    }
  }

  private def joolFunctionTypeOf(params: List[Type]) = s"Function${params.length}"
}
