package com.effiban.scala2java

import scala.meta.Term

object TermParamListTraverser {

  def traverse(termParams: List[Term.Param]): Unit = {
      ArgumentListTraverser.traverse(args = termParams,
        argTraverser = TermParamTraverser,
        maybeDelimiterType = Some(Parentheses))
  }
}
