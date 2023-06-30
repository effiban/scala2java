package io.github.effiban.scala2java.core.traversers

import scala.meta.{Decl, Defn, Stat, Term}

trait BlockStatTraverser extends ScalaTreeTraverser1[Stat]

private[traversers] class BlockStatTraverserImpl(expressionTermRefTraverser: => ExpressionTermRefTraverser,
                                                 defaultTermTraverser: => DefaultTermTraverser) extends BlockStatTraverser {

  override def traverse(stat: Stat): Stat = stat match {
    case termRef: Term.Ref => expressionTermRefTraverser.traverse(termRef)
    case term: Term => defaultTermTraverser.traverse(term)
    case defnVal: Defn.Val => defnVal //TODO
    case defnVar: Defn.Var => defnVar //TODO
    case declVar: Decl.Var => declVar //TODO
    // TODO support other stats once renderers are ready
    case aStat: Stat => throw new UnsupportedOperationException(s"Traversal of $aStat in a block is not supported yet")
  }
}
