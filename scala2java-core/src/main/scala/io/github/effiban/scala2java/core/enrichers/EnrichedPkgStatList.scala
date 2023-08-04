package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedStat
import io.github.effiban.scala2java.core.entities.SealedHierarchies

case class EnrichedPkgStatList(enrichedStats: List[EnrichedStat] = Nil,
                               sealedHierarchies: SealedHierarchies = SealedHierarchies())
