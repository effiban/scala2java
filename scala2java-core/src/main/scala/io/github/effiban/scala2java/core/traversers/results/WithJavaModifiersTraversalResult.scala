package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.JavaModifier

@deprecated
trait WithJavaModifiersTraversalResult {
  val javaModifiers: List[JavaModifier]
}
