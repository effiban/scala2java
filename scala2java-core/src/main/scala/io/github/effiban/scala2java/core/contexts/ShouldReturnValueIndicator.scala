package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.Decision.Decision

trait ShouldReturnValueIndicator {
  val shouldReturnValue: Decision
}
