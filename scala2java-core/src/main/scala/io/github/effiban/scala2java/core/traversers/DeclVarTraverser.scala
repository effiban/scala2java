package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext

import scala.meta.Decl

trait DeclVarTraverser {
  def traverse(varDecl: Decl.Var, context: StatContext = StatContext()): Decl.Var
}

private[traversers] class DeclVarTraverserImpl(statModListTraverser: => StatModListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patTraverser: => PatTraverser) extends DeclVarTraverser {

  //TODO replace interface data member (invalid in Java) with accessor method (+ mutator if not final)
  override def traverse(declVar: Decl.Var, context: StatContext = StatContext()): Decl.Var = {
    val traversedMods = statModListTraverser.traverse(declVar.mods)
    //TODO - verify when not simple case
    val traversedPats = declVar.pats.map(patTraverser.traverse)
    val traversedType = typeTraverser.traverse(declVar.decltpe)

    Decl.Var(
      mods = traversedMods,
      pats = traversedPats,
      decltpe = traversedType
    )
  }
}
