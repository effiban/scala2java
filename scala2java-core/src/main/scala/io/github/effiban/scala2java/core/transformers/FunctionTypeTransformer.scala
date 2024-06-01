package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TypeSelects._

import scala.meta.{Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

trait FunctionTypeTransformer {
  def transform(functionType: Type.Function): Type
}

private[transformers] class FunctionTypeTransformerImpl(treeTransformer: => TreeTransformer) extends FunctionTypeTransformer {

  // Transform a Scala function type (e.g.: Int => String) into a Java function type (e.g. Function<Int, String>)
  override def transform(functionType: Type.Function): Type = {
    val transformedParams = functionType.params.map(treeTransformer.transform(_).asInstanceOf[Type])
    val transformedRes = treeTransformer.transform(functionType.res).asInstanceOf[Type]

    (transformedParams, transformedRes) match {
      case (Nil | List(t"void") , t"void") => JavaRunnable
      case (Nil | List(t"void"), res) => Type.Apply(JavaSupplier, List(res))
      case (List(param), t"void") => Type.Apply(JavaConsumer, List(param))
      case (List(param), res) => Type.Apply(JavaFunction, List(param, res))
      case (List(param1, param2), t"void") => Type.Apply(JavaBiConsumer, List(param1, param2))
      case (List(param1, param2), res) => Type.Apply(JavaBiFunction, List(param1, param2, res))
      // Since Java doesn't have built-in support for a function type of more than 2 params, using JOOL instead
      case (params, res) => Type.Apply(joolFunctionTypeOf(params), params :+ res)
    }
  }

  private def joolFunctionTypeOf(params: List[Type]) = Type.Select(q"org.jooq.lambda.function", Type.Name(s"Function${params.length}"))
}
