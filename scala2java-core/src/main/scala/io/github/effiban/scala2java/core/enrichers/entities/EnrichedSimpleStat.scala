package io.github.effiban.scala2java.core.enrichers.entities

import scala.meta.Stat

case class EnrichedSimpleStat(override val stat: Stat) extends EnrichedStat
