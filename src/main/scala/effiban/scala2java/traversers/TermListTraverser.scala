package effiban.scala2java.traversers

import effiban.scala2java.contexts.TermContext
import effiban.scala2java.entities.ListTraversalOptions

import scala.meta.Term

trait TermListTraverser {

  def traverse(terms: List[Term],
               options: ListTraversalOptions = ListTraversalOptions(),
               context: TermContext = TermContext()): Unit
}

private[traversers] class TermListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                termTraverser: => TermTraverser) extends TermListTraverser {

  override def traverse(terms: List[Term],
                        options: ListTraversalOptions = ListTraversalOptions(),
                        context: TermContext = TermContext()): Unit = {
    argumentListTraverser.traverse(args = terms,
      argTraverser = (term: Term) => termTraverser.traverse(term, context),
      options = options)
  }
}
