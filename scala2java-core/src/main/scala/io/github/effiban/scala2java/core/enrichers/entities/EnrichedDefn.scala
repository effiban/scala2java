package io.github.effiban.scala2java.core.enrichers.entities

import scala.meta.Defn

trait EnrichedDefn extends EnrichedStatWithJavaModifiers {
  override val stat: Defn
}
