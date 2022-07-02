package com.effiban.scala2java.transformers

import scala.meta.Type

trait ScalaToJavaFunctionTypeTransformer {
  def transform(functionType: Type.Function): Type.Apply
}

private[scala2java] class ScalaToJavaFunctionTypeTransformerImpl extends ScalaToJavaFunctionTypeTransformer {

  // Transform a Scala function type (e.g.: Int => String) into a Java function type (e.g. Function<Int, String>)
  override def transform(functionType: Type.Function): Type.Apply = {
    //TODO - handle more than one input param (in Java we can use BiFunction for 2, for more we will need to create a special type or rely on libraries like Jool)
    Type.Apply(Type.Name("Function"), functionType.params :+ functionType.res)
  }
}

object ScalaToJavaFunctionTypeTransformer extends ScalaToJavaFunctionTypeTransformerImpl()