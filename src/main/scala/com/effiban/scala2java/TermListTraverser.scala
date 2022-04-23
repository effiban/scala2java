package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.DualDelimiterType

import scala.meta.Term

object TermListTraverser {

  def traverse(terms: List[Term],
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
