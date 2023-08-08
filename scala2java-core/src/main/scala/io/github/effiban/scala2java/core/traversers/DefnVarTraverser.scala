package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.traversers.results.{DeclVarTraversalResult, DefnVarTraversalResult, StatWithJavaModifiersTraversalResult}
import io.github.effiban.scala2java.spi.transformers.{DefnVarToDeclVarTransformer, DefnVarTransformer}

import scala.meta.Defn

trait DefnVarTraverser {
  def traverse(varDef: Defn.Var, context: StatContext = StatContext()): StatWithJavaModifiersTraversalResult
}

private[traversers] class DefnVarTraverserImpl(statModListTraverser: => StatModListTraverser,
                                               defnVarTypeTraverser: => DefnVarTypeTraverser,
                                               patTraverser: => PatTraverser,
                                               expressionTermTraverser: => ExpressionTermTraverser,
                                               declVarTraverser: => DeclVarTraverser,
                                               defnVarToDeclVarTransformer: DefnVarToDeclVarTransformer,
                                               defnVarTransformer: DefnVarTransformer) extends DefnVarTraverser {

  //TODO replace interface data member (invalid in Java) with accessor method (+ mutator if not 'final')
  override def traverse(defnVar: Defn.Var, context: StatContext = StatContext()): StatWithJavaModifiersTraversalResult = {
    defnVarToDeclVarTransformer.transform(defnVar, context.javaScope) match {
      case Some(varDecl) => DeclVarTraversalResult(declVarTraverser.traverse(varDecl, context))
      case None => traverseInner(defnVar, context)
    }
  }

  private def traverseInner(defnVar: Defn.Var, context: StatContext = StatContext()) = {
    val transformedDefnVar = defnVarTransformer.transform(defnVar, context.javaScope)
    val modListResult = statModListTraverser.traverse(ModifiersContext(transformedDefnVar, JavaTreeType.Variable, context.javaScope))
    //TODO - verify when not simple case
    val traversedPats = transformedDefnVar.pats.map(patTraverser.traverse)
    val maybeTraversedType = defnVarTypeTraverser.traverse(transformedDefnVar.decltpe, transformedDefnVar.rhs)
    val maybeTraversedRhs = transformedDefnVar.rhs.map(expressionTermTraverser.traverse)

    val traversedDefnVar = Defn.Var(
      mods = modListResult.scalaMods,
      pats = traversedPats,
      decltpe = maybeTraversedType,
      rhs = maybeTraversedRhs
    )
    DefnVarTraversalResult(traversedDefnVar, modListResult.javaModifiers)
  }
}
