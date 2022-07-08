package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter.EnclosingDelimiter

import scala.meta.Term

trait TermListTraverser {

  def traverse(terms: List[Term],
               onSameLine: Boolean = false,
               maybeEnclosingDelimiter: Option[EnclosingDelimiter] = None): Unit
}

private[scala2java] class TermListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                termTraverser: => TermTraverser) extends TermListTraverser {

  override def traverse(terms: List[Term],
                        onSameLine: Boolean = false,
                        maybeEnclosingDelimiter: Option[EnclosingDelimiter] = None): Unit = {
    if (terms.nonEmpty) {
      argumentListTraverser.traverse(args = terms,
        argTraverser = termTraverser,
        onSameLine = onSameLine,
        maybeEnclosingDelimiter = maybeEnclosingDelimiter)
    }
  }
}

object TermListTraverser extends TermListTraverserImpl(ArgumentListTraverser, TermTraverser)
