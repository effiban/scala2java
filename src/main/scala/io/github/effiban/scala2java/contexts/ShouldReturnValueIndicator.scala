package io.github.effiban.scala2java.contexts

import io.github.effiban.scala2java.entities.Decision.Decision

trait ShouldReturnValueIndicator {
  val shouldReturnValue: Decision
}
