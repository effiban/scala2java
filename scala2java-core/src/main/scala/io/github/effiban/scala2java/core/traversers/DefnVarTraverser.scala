package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.spi.transformers.{DefnVarToDeclVarTransformer, DefnVarTransformer}

import scala.meta.{Defn, Stat}

trait DefnVarTraverser {
  def traverse(varDef: Defn.Var, context: StatContext = StatContext()): Stat
}

private[traversers] class DefnVarTraverserImpl(statModListTraverser: => StatModListTraverser,
                                               defnVarTypeTraverser: => DefnVarTypeTraverser,
                                               patTraverser: => PatTraverser,
                                               expressionTermTraverser: => ExpressionTermTraverser,
                                               declVarTraverser: => DeclVarTraverser,
                                               defnVarToDeclVarTransformer: DefnVarToDeclVarTransformer,
                                               defnVarTransformer: DefnVarTransformer) extends DefnVarTraverser {

  //TODO replace interface data member (invalid in Java) with accessor method (+ mutator if not 'final')
  override def traverse(defnVar: Defn.Var, context: StatContext = StatContext()): Stat = {
    defnVarToDeclVarTransformer.transform(defnVar, context.javaScope) match {
      case Some(varDecl) => declVarTraverser.traverse(varDecl)
      case None => traverseInner(defnVar, context)
    }
  }

  private def traverseInner(defnVar: Defn.Var, context: StatContext = StatContext()) = {
    val transformedDefnVar = defnVarTransformer.transform(defnVar, context.javaScope)
    val traversedMods = statModListTraverser.traverse(transformedDefnVar.mods)
    //TODO - verify when not simple case
    val traversedPats = transformedDefnVar.pats.map(patTraverser.traverse)
    val maybeTraversedType = defnVarTypeTraverser.traverse(transformedDefnVar.decltpe, transformedDefnVar.rhs)
    val maybeTraversedRhs = transformedDefnVar.rhs.map(expressionTermTraverser.traverse)

    Defn.Var(
      mods = traversedMods,
      pats = traversedPats,
      decltpe = maybeTraversedType,
      rhs = maybeTraversedRhs
    )
  }
}
