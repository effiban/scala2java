package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.InvocationArgListContext
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerContextResolver
import io.github.effiban.scala2java.spi.transformers.{TermApplyToDefnDefTransformer, TermApplyTransformer}

import scala.meta.Term

trait TermApplyTraverser extends ScalaTreeTraverser[Term.Apply]

private[traversers] class TermApplyTraverserImpl(termTraverser: => TermTraverser,
                                                 arrayInitializerTraverser: => ArrayInitializerTraverser,
                                                 invocationArgListTraverser: => InvocationArgListTraverser,
                                                 defnDefTraverser: => DefnDefTraverser,
                                                 arrayInitializerContextResolver: ArrayInitializerContextResolver,
                                                 termApplyToDefnDefTransformer: TermApplyToDefnDefTransformer,
                                                 termApplyTransformer: TermApplyTransformer)
  extends TermApplyTraverser {

  // method invocation
  override def traverse(termApply: Term.Apply): Unit = {
    arrayInitializerContextResolver.tryResolve(termApply) match {
      case Some(context)  => arrayInitializerTraverser.traverseWithValues(context)
      case None => traverseMaybeAsDefnDef(termApply)
    }
  }

  private def traverseMaybeAsDefnDef(termApply: Term.Apply): Unit = {
    // TODO - call this only in a valid scope for a Java method definition
    termApplyToDefnDefTransformer.transform(termApply) match {
      case Some(defnDef) => defnDefTraverser.traverse(defnDef)
      case None => traverseRegular(termApply)
    }
  }

  private def traverseRegular(termApply: Term.Apply): Unit = {
    val transformedTermApply = termApplyTransformer.transform(termApply)
    termTraverser.traverse(transformedTermApply.fun)
    val invocationArgListContext = InvocationArgListContext(traverseEmpty = true, argNameAsComment = true)
    invocationArgListTraverser.traverse(transformedTermApply.args, invocationArgListContext)
  }
}
