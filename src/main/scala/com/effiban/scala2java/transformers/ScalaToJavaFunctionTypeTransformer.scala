package com.effiban.scala2java.transformers

import scala.meta.Type

trait ScalaToJavaFunctionTypeTransformer {
  def transform(functionType: Type.Function): Option[Type.Apply]
}

object ScalaToJavaFunctionTypeTransformer extends ScalaToJavaFunctionTypeTransformer {

  // Transform a Scala function type (e.g.: Int => String) into a Java function type (e.g. Function<Int, String>)
  override def transform(functionType: Type.Function): Option[Type.Apply] = {
    functionType.params match {
      case Nil => Some(Type.Apply(Type.Name("Supplier"), List(functionType.res)))
      case param :: Nil => Some(Type.Apply(Type.Name("Function"), List(param, functionType.res)))
      case param1 :: param2 :: Nil => Some(Type.Apply(Type.Name("BiFunction"), List(param1, param2, functionType.res)))
      //TODO - handle this with @FunctionalInterface or JOOL
      case params => None
    }

  }
}
