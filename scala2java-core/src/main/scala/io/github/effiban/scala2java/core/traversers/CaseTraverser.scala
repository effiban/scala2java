package io.github.effiban.scala2java.core.traversers

import scala.meta.Case

trait CaseTraverser extends ScalaTreeTraverser1[Case]

private[traversers] class CaseTraverserImpl(patTraverser: => PatTraverser,
                                            expressionTermTraverser: => ExpressionTermTraverser)
  extends CaseTraverser {

  def traverse(`case`: Case): Case = {
    import `case`._

    Case(
      pat = patTraverser.traverse(pat),
      cond = cond.map(expressionTermTraverser.traverse),
      body = expressionTermTraverser.traverse(body)
    )
  }
}
