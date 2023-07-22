package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.traversers.results.{StatWithJavaModifiersTraversalResult, UnsupportedDefnTraversalResult}

import scala.meta.Defn
import scala.meta.Defn.Trait

trait DefnTraverser {
  def traverse(defn: Defn, context: StatContext = StatContext()): StatWithJavaModifiersTraversalResult
}

private[traversers] class DefnTraverserImpl(defnVarTraverser: => DefnVarTraverser,
                                            defnDefTraverser: => DefnDefTraverser,
                                            traitTraverser: => TraitTraverser,
                                            objectTraverser: => ObjectTraverser) extends DefnTraverser {

  override def traverse(defn: Defn, context: StatContext = StatContext()): StatWithJavaModifiersTraversalResult = defn match {
    case defnVar: Defn.Var => defnVarTraverser.traverse(defnVar, context)
    case defnDef: Defn.Def => defnDefTraverser.traverse(defnDef, DefnDefContext(context.javaScope))
    case defnTrait: Trait => traitTraverser.traverse(defnTrait, ClassOrTraitContext(context.javaScope))
    case defnClass: Defn.Class => UnsupportedDefnTraversalResult(defnClass) // TODO
    case defnObject: Defn.Object => objectTraverser.traverse(defnObject, context)
    case unsupported => UnsupportedDefnTraversalResult(unsupported)
  }
}
