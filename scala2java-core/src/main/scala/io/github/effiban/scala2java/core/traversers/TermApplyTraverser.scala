package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArrayInitializerValuesContext
import io.github.effiban.scala2java.core.factories.TermApplyTransformationContextFactory
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerContextResolver
import io.github.effiban.scala2java.core.transformers.InternalTermApplyTransformer

import scala.meta.Term

trait TermApplyTraverser extends ScalaTreeTraverser1[Term.Apply]

private[traversers] class TermApplyTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                                 arrayInitializerTraverser: => ArrayInitializerTraverser,
                                                 termApplyTransformationContextFactory: TermApplyTransformationContextFactory,
                                                 arrayInitializerContextResolver: ArrayInitializerContextResolver,
                                                 termApplyTransformer: InternalTermApplyTransformer) extends TermApplyTraverser {

  // method invocation
  override def traverse(termApply: Term.Apply): Term.Apply = {
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

  private def traverseMethodInvocation(termApply: Term.Apply): Term.Apply = {
    val transformationContext = termApplyTransformationContextFactory.create(termApply)
    val transformedTermApply = termApplyTransformer.transform(termApply, transformationContext)
    val traversedFun = expressionTermTraverser.traverse(transformedTermApply.fun)
    val traversedArgs = transformedTermApply.args.map(expressionTermTraverser.traverse)
    Term.Apply(traversedFun, traversedArgs)
  }
}
