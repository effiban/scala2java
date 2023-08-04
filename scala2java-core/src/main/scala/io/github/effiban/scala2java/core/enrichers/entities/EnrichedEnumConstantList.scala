package io.github.effiban.scala2java.core.enrichers.entities

import scala.meta.Defn

case class EnrichedEnumConstantList(override val stat: Defn.Var) extends EnrichedStat
