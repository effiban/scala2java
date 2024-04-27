package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.typeinference.FunctionTypeInferrer

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

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
      case (t"Runnable", q"apply") => q"run"
      case (Type.Apply(t"java.util.function.Supplier", _), q"apply") => q"get"
      case (Type.Apply(t"java.util.function.Consumer" | t"java.util.function.BiConsumer", _), q"apply") => q"accept"
      case (_, methodName) => methodName
    }
    Term.Select(Term.Ascribe(termFunction, transformedFunctionType), transformedMethodName)
  }
}
