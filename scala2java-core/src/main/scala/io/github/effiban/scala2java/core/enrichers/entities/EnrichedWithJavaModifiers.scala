package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.JavaModifier

trait EnrichedWithJavaModifiers {
  val javaModifiers: List[JavaModifier]
}
