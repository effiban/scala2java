package effiban.scala2java.traversers

import effiban.scala2java.entities.ListTraversalOptions

import scala.meta.Term

trait TermListTraverser {

  def traverse(terms: List[Term],
               options: ListTraversalOptions = ListTraversalOptions()): Unit
}

private[traversers] class TermListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                termTraverser: => TermTraverser) extends TermListTraverser {

  override def traverse(terms: List[Term],
                        options: ListTraversalOptions = ListTraversalOptions()): Unit = {
    argumentListTraverser.traverse(args = terms,
      argTraverser = termTraverser,
      options = options)
  }
}
