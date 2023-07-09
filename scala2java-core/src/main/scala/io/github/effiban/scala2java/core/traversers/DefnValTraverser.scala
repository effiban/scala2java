package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.traversers.results.{DefnValTraversalResult, StatWithJavaModifiersTraversalResult}
import io.github.effiban.scala2java.spi.transformers.DefnValToDeclVarTransformer

import scala.meta.Defn

trait DefnValTraverser {
  def traverse(defnVal: Defn.Val, context: StatContext = StatContext()): StatWithJavaModifiersTraversalResult
}

private[traversers] class DefnValTraverserImpl(modListTraverser: => ModListTraverser,
                                               defnValOrVarTypeTraverser: => DefnValOrVarTypeTraverser,
                                               patTraverser: => PatTraverser,
                                               expressionTermTraverser: => ExpressionTermTraverser,
                                               declVarTraverser: => DeclVarTraverser,
                                               defnValToDeclVarTransformer: DefnValToDeclVarTransformer) extends DefnValTraverser {

  //TODO if it is non-public it will be invalid in a Java interface - replace with method
  override def traverse(valDef: Defn.Val, context: StatContext = StatContext()): StatWithJavaModifiersTraversalResult = {
    defnValToDeclVarTransformer.transform(valDef, context.javaScope) match {
      case Some(varDecl) => declVarTraverser.traverse(varDecl, context)
      case None => traverseInner(valDef, context)
    }
  }

  private def traverseInner(valDef: Defn.Val, context: StatContext) = {
    val modListResult = modListTraverser.traverse(ModifiersContext(valDef, JavaTreeType.Variable, context.javaScope))
    val traversedPats = valDef.pats.map(patTraverser.traverse)
    val maybeTraversedType = defnValOrVarTypeTraverser.traverse(valDef.decltpe, Some(valDef.rhs))
    //TODO verify for non-simple case
    val traversedRhs = expressionTermTraverser.traverse(valDef.rhs)

    val traversedDefnVal = Defn.Val(
      mods = modListResult.scalaMods,
      pats = traversedPats,
      decltpe = maybeTraversedType,
      rhs = traversedRhs
    )
    DefnValTraversalResult(traversedDefnVal, modListResult.javaModifiers)
  }
}
