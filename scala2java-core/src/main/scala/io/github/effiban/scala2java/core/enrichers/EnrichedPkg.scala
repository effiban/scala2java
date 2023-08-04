package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedStat
import io.github.effiban.scala2java.core.entities.SealedHierarchies

import scala.meta.{Pkg, Term}

case class EnrichedPkg(pkgRef: Term.Ref,
                       enrichedStats: List[EnrichedStat] = Nil,
                       sealedHierarchies: SealedHierarchies = SealedHierarchies()) extends EnrichedStat {

  override val stat: Pkg = Pkg(
    ref = pkgRef,
    stats = enrichedStats.map(_.stat)
  )
}
