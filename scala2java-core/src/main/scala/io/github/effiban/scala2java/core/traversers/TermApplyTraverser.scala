package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArrayInitializerValuesContext
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerContextResolver

import scala.annotation.tailrec
import scala.meta.Term

trait TermApplyTraverser extends ScalaTreeTraverser1[Term.Apply]

private[traversers] class TermApplyTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                                 arrayInitializerTraverser: => ArrayInitializerTraverser,
                                                 arrayInitializerContextResolver: ArrayInitializerContextResolver) extends TermApplyTraverser {

  // method invocation
  @tailrec
  final override def traverse(termApply: Term.Apply): Term.Apply = {
    termApply match {
      case Term.Apply(Term.Apply(fun, args1), args2) => traverse(Term.Apply(fun, args1 ++ args2))
      case aTermApply => traverseUncurried(aTermApply)
    }
  }

  private def traverseUncurried(termApply: Term.Apply): Term.Apply = {
    arrayInitializerContextResolver.tryResolve(termApply) match {
      case Some(context) => traverseArrayInitializer(termApply, context)
      case None => traverseMethodInvocation(termApply)
    }
  }

  private def traverseArrayInitializer(termApply: Term.Apply, context: ArrayInitializerValuesContext) = {
    val outputContext = arrayInitializerTraverser.traverseWithValues(context)
    val traversedFun = termApply.fun match {
      case fun@(_: Term.Name | _: Term.Select) => Term.ApplyType(fun, List(outputContext.tpe))
      case fun: Term.ApplyType => fun.copy(targs = List(outputContext.tpe))
      case fun => fun
    }
    Term.Apply(traversedFun, outputContext.values)
  }

  private def traverseMethodInvocation(aTermApply: Term.Apply) = {
    val traversedFun = expressionTermTraverser.traverse(aTermApply.fun)
    val traversedArgs = aTermApply.args.map(expressionTermTraverser.traverse)
    Term.Apply(traversedFun, traversedArgs)
  }
}
