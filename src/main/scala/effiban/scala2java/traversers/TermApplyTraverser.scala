package effiban.scala2java.traversers

import effiban.scala2java.contexts.InvocationArgListContext
import effiban.scala2java.transformers.TermApplyTransformer

import scala.meta.Term

trait TermApplyTraverser extends ScalaTreeTraverser[Term.Apply]

private[traversers] class TermApplyTraverserImpl(termTraverser: => TermTraverser,
                                                 invocationArgListTraverser: => InvocationArgListTraverser,
                                                 termApplyTransformer: TermApplyTransformer) extends TermApplyTraverser {

  // method invocation
  override def traverse(termApply: Term.Apply): Unit = {
    val javaTermApply = termApplyTransformer.transform(termApply)
    termTraverser.traverse(javaTermApply.fun)
    val invocationArgListContext = InvocationArgListContext(traverseEmpty = true, argNameAsComment = true)
    invocationArgListTraverser.traverse(javaTermApply.args, invocationArgListContext)
  }
}
