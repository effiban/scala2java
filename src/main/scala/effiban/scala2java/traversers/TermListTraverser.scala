package effiban.scala2java.traversers

import effiban.scala2java.entities.DualDelimiterType

import scala.meta.Term

trait TermListTraverser {

  def traverse(terms: List[Term],
               onSameLine: Boolean = false,
               maybeDelimiterType: Option[DualDelimiterType] = None): Unit
}

private[scala2java] class TermListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                termTraverser: => TermTraverser) extends TermListTraverser {

  override def traverse(terms: List[Term],
                        onSameLine: Boolean = false,
                        maybeDelimiterType: Option[DualDelimiterType] = None): Unit = {
    if (terms.nonEmpty) {
      argumentListTraverser.traverse(args = terms,
        argTraverser = termTraverser,
        onSameLine = onSameLine,
        maybeDelimiterType = maybeDelimiterType)
    }
  }
}

object TermListTraverser extends TermListTraverserImpl(ArgumentListTraverser, TermTraverser)
