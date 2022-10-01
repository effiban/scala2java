package effiban.scala2java.traversers

import effiban.scala2java.contexts.{InvocationArgContext, InvocationArgListContext}
import effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import effiban.scala2java.entities.ListTraversalOptions

import scala.meta.Term

trait InvocationArgListTraverser {

  def traverse(args: List[Term],
               context: InvocationArgListContext = InvocationArgListContext()): Unit
}

private[traversers] class InvocationArgListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                         invocationArgTraverser: => InvocationArgTraverser) extends InvocationArgListTraverser {

  override def traverse(args: List[Term],
                        context: InvocationArgListContext = InvocationArgListContext()): Unit = {
    val options = ListTraversalOptions(
      onSameLine = context.onSameLine,
      maybeEnclosingDelimiter = Some(Parentheses),
      traverseEmpty = context.traverseEmpty
    )
    val invocationArgContext = InvocationArgContext(context.argNameAsComment)

    argumentListTraverser.traverse(args = args,
      argTraverser = (arg: Term) => invocationArgTraverser.traverse(arg, invocationArgContext),
      options = options)
  }
}
