package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.InvocationArgListContext
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerContextResolver
import io.github.effiban.scala2java.core.transformers.TermApplyTransformer

import scala.meta.Term

trait TermApplyTraverser extends ScalaTreeTraverser[Term.Apply]

private[traversers] class TermApplyTraverserImpl(termTraverser: => TermTraverser,
                                                 arrayInitializerTraverser: => ArrayInitializerTraverser,
                                                 invocationArgListTraverser: => InvocationArgListTraverser,
                                                 arrayInitializerContextResolver: ArrayInitializerContextResolver,
                                                 termApplyTransformer: TermApplyTransformer)
  extends TermApplyTraverser {

  // method invocation
  override def traverse(termApply: Term.Apply): Unit = {
    arrayInitializerContextResolver.tryResolve(termApply) match {
      case Some(context)  => arrayInitializerTraverser.traverseWithValues(context)
      case None => traverseRegular(termApply)
    }
  }

  private def traverseRegular(trmApply: Term.Apply): Unit = {
    val transformedTermApply = termApplyTransformer.transform(trmApply)
    termTraverser.traverse(transformedTermApply.fun)
    val invocationArgListContext = InvocationArgListContext(traverseEmpty = true, argNameAsComment = true)
    invocationArgListTraverser.traverse(transformedTermApply.args, invocationArgListContext)
  }
}
