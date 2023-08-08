package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.traversers.results.UnsupportedDefnTraversalResult

import scala.meta.Defn.Trait
import scala.meta.{Defn, Stat}

trait DefnTraverser {
  def traverse(defn: Defn, context: StatContext = StatContext()): Stat
}

private[traversers] class DefnTraverserImpl(defnVarTraverser: => DefnVarTraverser,
                                            defnDefTraverser: => DefnDefTraverser,
                                            traitTraverser: => TraitTraverser,
                                            classTraverser: => ClassTraverser,
                                            objectTraverser: => ObjectTraverser) extends DefnTraverser {

  override def traverse(defn: Defn, context: StatContext = StatContext()): Stat = defn match {
    case defnVar: Defn.Var => defnVarTraverser.traverse(defnVar, context)
    case defnDef: Defn.Def => defnDefTraverser.traverse(defnDef, DefnDefContext(context.javaScope)).tree
    case defnTrait: Trait => traitTraverser.traverse(defnTrait)
    case defnClass: Defn.Class => classTraverser.traverse(defnClass, ClassOrTraitContext(context.javaScope))
    case defnObject: Defn.Object => objectTraverser.traverse(defnObject, context)
    case unsupported => UnsupportedDefnTraversalResult(unsupported).tree
  }
}
