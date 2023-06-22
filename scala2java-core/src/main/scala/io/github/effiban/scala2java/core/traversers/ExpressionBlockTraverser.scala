package io.github.effiban.scala2java.core.traversers

import scala.meta.Term.Block
import scala.meta.{Term, XtensionQuasiquoteTerm}

trait ExpressionBlockTraverser extends ScalaTreeTraverser2[Block, Term]

private[traversers] class ExpressionBlockTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser)
  extends ExpressionBlockTraverser {

  // A Block is not a valid expression in Java, so we need to transform it before traversal
  override def traverse(block: Block): Term = {
    val traversedTerm = block match {
      // When it is a block with one term - traverse it directly
      case Block(List(term: Term)) => term
      // When it has a single non-term statement (not sure this is possible?), or else more than one statement -
      // wrapping with a zero-arg lambda which is immediately invoked
      case block: Block => Term.Apply(Term.Select(Term.Function(Nil, block), q"apply"), Nil)
    }
    expressionTermTraverser.traverse(traversedTerm)
  }
}
