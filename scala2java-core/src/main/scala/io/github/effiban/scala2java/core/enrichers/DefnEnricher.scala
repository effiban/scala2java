package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedDefn, EnrichedUnsupportedDefn}

import scala.meta.Defn
import scala.meta.Defn.Trait

trait DefnEnricher {
  def enrich(defn: Defn, context: StatContext = StatContext()): EnrichedDefn

}

private[enrichers] class DefnEnricherImpl(defnVarEnricher: DefnVarEnricher,
                                          defnDefEnricher: DefnDefEnricher) extends DefnEnricher {

  override def enrich(defn: Defn, context: StatContext = StatContext()): EnrichedDefn = defn match {
    case defnVar: Defn.Var => defnVarEnricher.enrich(defnVar, context)
    case defnDef: Defn.Def => defnDefEnricher.enrich(defnDef, context)
    case defnTrait: Trait => EnrichedUnsupportedDefn(defnTrait) // TODO
    case defnClass: Defn.Class => EnrichedUnsupportedDefn(defnClass) // TODO
    case defnObject: Defn.Object => EnrichedUnsupportedDefn(defnObject) // TODO
    case defn => EnrichedUnsupportedDefn(defn)
  }
}

object DefnEnricher extends DefnEnricherImpl(DefnVarEnricher, DefnDefEnricher)