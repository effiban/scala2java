package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.{Decl, Defn, Stat, Term}

trait BlockStatTraverser extends ScalaTreeTraverser1[Stat]

private[traversers] class BlockStatTraverserImpl(statTermTraverser: => StatTermTraverser,
                                                 defnVarTraverser: => DefnVarTraverser,
                                                 declVarTraverser: => DeclVarTraverser) extends BlockStatTraverser {

  override def traverse(stat: Stat): Stat = stat match {
    case term: Term => statTermTraverser.traverse(term)
    case defnVar: Defn.Var => traverseDefnVar(defnVar)
    case declVar: Decl.Var => traverseDeclVar(declVar)
    // TODO support Class/Trait/Object
    case aStat: Stat => throw new UnsupportedOperationException(s"Traversal of $aStat in a block is not supported yet")
  }

  private def traverseDefnVar(defnVar: Defn.Var) = {
    defnVarTraverser.traverse(defnVar, StatContext(JavaScope.Block))
  }

  private def traverseDeclVar(declVar: Decl.Var) = {
    declVarTraverser.traverse(declVar)
  }
}
