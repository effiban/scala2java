package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

trait TermMatchTraverser extends ScalaTreeTraverser1[Term.Match]

private[traversers] class TermMatchTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                                 caseTraverser: => CaseTraverser) extends TermMatchTraverser {

  override def traverse(termMatch: Term.Match): Term.Match = {
    import termMatch._

    Term.Match(
      expr = expressionTermTraverser.traverse(expr),
      cases = cases.map(caseTraverser.traverse)
    )
  }
}
