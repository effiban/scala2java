package com.effiban.scala2java

import com.effiban.scala2java.transformers.ScalaToJavaFunctionTypeTransformer

import scala.meta.Type

trait TypeFunctionTraverser extends ScalaTreeTraverser[Type.Function]

private[scala2java] class TypeFunctionTraverserImpl(typeApplyTraverser: => TypeApplyTraverser,
                                                    scalaToJavaFunctionTypeTransformer: ScalaToJavaFunctionTypeTransformer) extends TypeFunctionTraverser {

  // function type, e.g.: Int => String
  override def traverse(functionType: Type.Function): Unit = {
    typeApplyTraverser.traverse(scalaToJavaFunctionTypeTransformer.transform(functionType))
  }
}

object TypeFunctionTraverser extends TypeFunctionTraverserImpl(TypeApplyTraverser, ScalaToJavaFunctionTypeTransformer)