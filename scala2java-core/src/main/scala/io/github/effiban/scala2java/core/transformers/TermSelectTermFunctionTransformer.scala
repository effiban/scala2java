package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TermNameValues.{Apply, Get, JavaAccept, JavaRun}
import io.github.effiban.scala2java.core.entities.TypeNameValues.{JavaBiConsumer, JavaConsumer, JavaRunnable, JavaSupplier}
import io.github.effiban.scala2java.core.typeinference.FunctionTypeInferrer

import scala.meta.{Term, Type}

/** Transforms a qualified expression of a Scala lambda plus a method name, e.g. :
 * {{{
 *   (() => 3).apply
 * }}}
 * In Java we need to cast the lambda to a functional interface and translate the method, e.g. :
 * {{{
 *   ((Supplier<Integer>)__ -> 3).get
 * }}}
 *
 * A Scala lambda invocation rarely appears explicitly - but it can arise as the result of another transformation
 * performed at an earlier phase by the tool.
 */
trait TermSelectTermFunctionTransformer {
  def transform(termFunction: Term.Function, methodName: Term.Name): Term.Select
}

class TermSelectTermFunctionTransformerImpl(functionTypeInferrer: => FunctionTypeInferrer,
                                            functionTypeTransformer: FunctionTypeTransformer) extends TermSelectTermFunctionTransformer {

  override def transform(termFunction: Term.Function, methodName: Term.Name): Term.Select = {
    val transformedFunctionType = functionTypeTransformer.transform(functionTypeInferrer.infer(termFunction))
    val transformedMethodName = (transformedFunctionType, methodName) match {
      case (Type.Name(JavaRunnable), Term.Name(Apply)) => Term.Name(JavaRun)
      case (Type.Apply(Type.Name(JavaSupplier), _), Term.Name(Apply)) => Term.Name(Get)
      case (Type.Apply(Type.Name(JavaConsumer) | Type.Name(JavaBiConsumer), _), Term.Name(Apply)) => Term.Name(JavaAccept)
      case (_, methodName) => methodName
    }
    Term.Select(Term.Ascribe(termFunction, transformedFunctionType), transformedMethodName)
  }
}
