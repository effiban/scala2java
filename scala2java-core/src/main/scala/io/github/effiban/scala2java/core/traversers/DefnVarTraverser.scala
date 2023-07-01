package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.traversers.results.DefnVarTraversalResult

import scala.meta.Defn

trait DefnVarTraverser {
  def traverse(varDef: Defn.Var, context: StatContext = StatContext()): DefnVarTraversalResult
}

private[traversers] class DefnVarTraverserImpl(modListTraverser: => ModListTraverser,
                                               defnValOrVarTypeTraverser: => DefnValOrVarTypeTraverser,
                                               patTraverser: => PatTraverser,
                                               expressionTermTraverser: => ExpressionTermTraverser) extends DefnVarTraverser {

  //TODO replace mutable interface data member (invalid in Java) with accessor/mutator methods
  override def traverse(defnVar: Defn.Var, context: StatContext = StatContext()): DefnVarTraversalResult = {
    val modListResult = modListTraverser.traverse(ModifiersContext(defnVar, JavaTreeType.Variable, context.javaScope))
    //TODO - verify when not simple case
    val traversedPats = defnVar.pats.map(patTraverser.traverse)
    val maybeTraversedType = defnValOrVarTypeTraverser.traverse(defnVar.decltpe, defnVar.rhs)
    val maybeTraversedRhs = defnVar.rhs.map(expressionTermTraverser.traverse)

    val traversedDefnVar = Defn.Var(
      mods = modListResult.scalaMods,
      pats = traversedPats,
      decltpe = maybeTraversedType,
      rhs = maybeTraversedRhs
    )
    DefnVarTraversalResult(traversedDefnVar, modListResult.javaModifiers)
  }
}
