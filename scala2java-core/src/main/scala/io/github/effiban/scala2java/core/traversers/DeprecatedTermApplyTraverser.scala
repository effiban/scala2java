package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.factories.TermApplyTransformationContextFactory
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerContextResolver
import io.github.effiban.scala2java.core.transformers.InternalTermApplyTransformer

import scala.meta.Term

@deprecated
trait DeprecatedTermApplyTraverser extends ScalaTreeTraverser[Term.Apply]

@deprecated
private[traversers] class DeprecatedTermApplyTraverserImpl(expressionTermTraverser: => DeprecatedExpressionTermTraverser,
                                                           arrayInitializerTraverser: => DeprecatedArrayInitializerTraverser,
                                                           argumentListTraverser: => DeprecatedArgumentListTraverser,
                                                           invocationArgTraverser: => DeprecatedArgumentTraverser[Term],
                                                           termApplyTransformationContextFactory: TermApplyTransformationContextFactory,
                                                           arrayInitializerContextResolver: ArrayInitializerContextResolver,
                                                           termApplyTransformer: InternalTermApplyTransformer) extends DeprecatedTermApplyTraverser {

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
