package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedStat
import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.{Defn, Name, Stat}

trait PkgStatEnricher {
  def enrich(stat: Stat, sealedHierarchies: SealedHierarchies): EnrichedStat
}

private[enrichers] class PkgStatEnricherImpl(classEnricher: => ClassEnricher,
                                             traitEnricher: => TraitEnricher,
                                             objectEnricher: => ObjectEnricher,
                                             defaultStatEnricher: => DefaultStatEnricher) extends PkgStatEnricher {

  override def enrich(stat: Stat, sealedHierarchies: SealedHierarchies): EnrichedStat = {
    stat match {
      case `class`: Defn.Class => classEnricher.enrich(`class`, generateContext(`class`.name, sealedHierarchies))
      case `trait`: Defn.Trait => traitEnricher.enrich(`trait`, generateContext(`trait`.name, sealedHierarchies))
      case `object`: Defn.Object => objectEnricher.enrich(`object`, generateContext(`object`.name, sealedHierarchies))
      case stat => defaultStatEnricher.enrich(stat, StatContext(JavaScope.Package))
    }
  }

  private def generateContext(name: Name, sealedHierarchies: SealedHierarchies) = {
    StatContext(resolveJavaScope(name, sealedHierarchies))
  }

  private def resolveJavaScope(name: Name, sealedHierarchies: SealedHierarchies) = {
    if (sealedHierarchies.isSubType(name)) JavaScope.Sealed else JavaScope.Package
  }
}
