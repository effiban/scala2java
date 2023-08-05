package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedSimpleStat, EnrichedStat}

import scala.meta.{Decl, Defn, Pkg, Stat}

trait DefaultStatEnricher {
  def enrich(stat: Stat, statContext: StatContext = StatContext()): EnrichedStat
}

private[enrichers] class DefaultStatEnricherImpl(pkgEnricher: => PkgEnricher,
                                                 defnEnricher: => DefnEnricher,
                                                 declEnricher: DeclEnricher) extends DefaultStatEnricher {

  override def enrich(stat: Stat, statContext: StatContext = StatContext()): EnrichedStat = stat match {
    case pkg: Pkg => pkgEnricher.enrich(pkg)
    case defn: Defn => defnEnricher.enrich(defn, statContext)
    case decl: Decl => declEnricher.enrich(decl, statContext)
    case other => EnrichedSimpleStat(other)
  }
}
