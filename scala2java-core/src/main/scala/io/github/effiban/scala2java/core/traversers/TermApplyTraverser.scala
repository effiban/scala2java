package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.factories.TermApplyTransformationContextFactory
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerContextResolver
import io.github.effiban.scala2java.core.transformers.InternalTermApplyTransformer

import scala.meta.Term

trait TermApplyTraverser extends ScalaTreeTraverser[Term.Apply]

private[traversers] class TermApplyTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                                 arrayInitializerTraverser: => ArrayInitializerTraverser,
                                                 argumentListTraverser: => ArgumentListTraverser,
                                                 invocationArgTraverser: => ArgumentTraverser[Term],
                                                 termApplyTransformationContextFactory: TermApplyTransformationContextFactory,
                                                 arrayInitializerContextResolver: ArrayInitializerContextResolver,
                                                 termApplyTransformer: InternalTermApplyTransformer) extends TermApplyTraverser {

  // method invocation
  override def traverse(termApply: Term.Apply): Unit = {
    arrayInitializerContextResolver.tryResolve(termApply) match {
      case Some(context)  => arrayInitializerTraverser.traverseWithValues(context)
      case None => traverseRegular(termApply)
    }
  }

  private def traverseRegular(termApply: Term.Apply): Unit = {
    val transformationContext = termApplyTransformationContextFactory.create(termApply)
    val transformedTermApply = termApplyTransformer.transform(termApply, transformationContext)
    expressionTermTraverser.traverse(transformedTermApply.fun)
    val options = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses), traverseEmpty = true)
    val argListContext = ArgumentListContext(options = options, argNameAsComment = true)
    argumentListTraverser.traverse(
      args = transformedTermApply.args,
      argTraverser = invocationArgTraverser,
      context = argListContext
    )
  }
}
