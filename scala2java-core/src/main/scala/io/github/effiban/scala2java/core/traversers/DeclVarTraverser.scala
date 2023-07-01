package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.traversers.results.DeclVarTraversalResult

import scala.meta.Decl

trait DeclVarTraverser {
  def traverse(varDecl: Decl.Var, context: StatContext = StatContext()): DeclVarTraversalResult
}

private[traversers] class DeclVarTraverserImpl(modListTraverser: => ModListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patTraverser: => PatTraverser) extends DeclVarTraverser {

  //TODO replace mutable interface data member (invalid in Java) with accessor / mutator methods
  override def traverse(declVar: Decl.Var, context: StatContext = StatContext()): DeclVarTraversalResult = {
    val modListResult = modListTraverser.traverse(ModifiersContext(declVar, JavaTreeType.Variable, context.javaScope))
    //TODO - verify when not simple case
    val traversedPats = declVar.pats.map(patTraverser.traverse)
    val traversedType = typeTraverser.traverse(declVar.decltpe)

    val traversedDeclVar = Decl.Var(
      mods = modListResult.scalaMods,
      pats = traversedPats,
      decltpe = traversedType
    )
    DeclVarTraversalResult(traversedDeclVar, modListResult.javaModifiers)
  }
}
