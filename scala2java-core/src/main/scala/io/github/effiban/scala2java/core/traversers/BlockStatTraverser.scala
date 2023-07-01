package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.{Decl, Defn, Stat, Term}

trait BlockStatTraverser extends ScalaTreeTraverser1[Stat]

private[traversers] class BlockStatTraverserImpl(expressionTermRefTraverser: => ExpressionTermRefTraverser,
                                                 defaultTermTraverser: => DefaultTermTraverser,
                                                 defnVarTraverser: => DefnVarTraverser,
                                                 declVarTraverser: => DeclVarTraverser) extends BlockStatTraverser {

  override def traverse(stat: Stat): Stat = stat match {
    case termRef: Term.Ref => expressionTermRefTraverser.traverse(termRef)
    case term: Term => defaultTermTraverser.traverse(term)
    case defnVal: Defn.Val => defnVal //TODO
    case defnVar: Defn.Var => traverseDefnVar(defnVar)
    case declVar: Decl.Var => traverseDeclVar(declVar)
    // TODO support other stats once renderers are ready
    case aStat: Stat => throw new UnsupportedOperationException(s"Traversal of $aStat in a block is not supported yet")
  }

  private def traverseDefnVar(defnVar: Defn.Var) = {
    defnVarTraverser.traverse(defnVar, StatContext(JavaScope.Block)).defnVar
  }

  private def traverseDeclVar(declVar: Decl.Var) = {
    declVarTraverser.traverse(declVar, StatContext(JavaScope.Block)).declVar
  }
}
