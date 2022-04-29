package com.effiban.scala2java

import scala.meta.Term

trait TermListTraverser {

  def traverse(terms: List[Term],
               onSameLine: Boolean = false,
               maybeDelimiterType: Option[DualDelimiterType] = None): Unit
}

object TermListTraverser extends TermListTraverser {

  override def traverse(terms: List[Term],
                        onSameLine: Boolean = false,
                        maybeDelimiterType: Option[DualDelimiterType] = None): Unit = {
    if (terms.nonEmpty) {
      ArgumentListTraverser.traverse(args = terms,
        argTraverser = TermTraverser,
        onSameLine = onSameLine,
        maybeDelimiterType = maybeDelimiterType)
    }
  }
}
